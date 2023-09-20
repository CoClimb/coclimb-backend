package swm.s3.coclimb.api.adapter.out.instagram;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import swm.s3.coclimb.api.adapter.out.instagram.dto.InstagramMediaResponseDto;
import swm.s3.coclimb.api.adapter.out.instagram.dto.LongLivedTokenResponse;
import swm.s3.coclimb.api.adapter.out.instagram.dto.ShortLivedTokenResponse;
import swm.s3.coclimb.api.exception.errortype.instagram.*;

import java.util.List;
import java.util.Map;


@Component
@RequiredArgsConstructor
public class InstagramRestApi {

    private final WebClient graphClient =WebClient.create("https://graph.instagram.com");;
    private final WebClient basicDisplayClient =WebClient.create("https://api.instagram.com");
    private final InstagramOAuthRecord instagramOAuthRecord;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ShortLivedTokenResponse getShortLivedTokenAndUserId(String code) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_id", instagramOAuthRecord.getClientId());
        formData.add("client_secret", instagramOAuthRecord.getClientSecret());
        formData.add("grant_type", "authorization_code");
        formData.add("redirect_uri", instagramOAuthRecord.getRedirectUri());
        formData.add("code", code);

        String response = basicDisplayClient.post()
                .uri("/oauth/access_token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .exchangeToMono(clientResponse -> {
                    if (clientResponse.statusCode().is2xxSuccessful()) {
                        return clientResponse.bodyToMono(String.class);
                    } else if(clientResponse.statusCode().is4xxClientError()){
                        return Mono.error(new IssueInstagramShortLivedTokenFail());
                    }else {
                        return Mono.error(new IssueInstagramShortLivedTokenFail());
                    }
                }).block();
        return getTokenResponseDto(response, ShortLivedTokenResponse.class);
    }

    protected LongLivedTokenResponse getLongLivedToken(String shortLivedToken) {
        String targetUri = String.format("/access_token?grant_type=ig_exchange_token&client_secret=%s&access_token=%s",
                instagramOAuthRecord.getClientSecret(), shortLivedToken);


        String response = graphClient.get()
                .uri(targetUri)
                .exchangeToMono(clientResponse -> {
                    if (clientResponse.statusCode().is2xxSuccessful()) {
                        return clientResponse.bodyToMono(String.class);
                    } else {
                        return Mono.error(new IssueInstagramLongLivedTokenFail());
                    }
                }).block();

        return getTokenResponseDto(response, LongLivedTokenResponse.class);
    }

    protected LongLivedTokenResponse refreshLongLivedToken(String longLivedToken) {
        String targetUri = String.format("/refresh_access_token?grant_type=ig_refresh_token&access_token=%s",
                longLivedToken);

        String response = graphClient.get()
                .uri(targetUri)
                .exchangeToMono(clientResponse -> {
                    if (clientResponse.statusCode().is2xxSuccessful()) {
                        return clientResponse.bodyToMono(String.class);
                    } else {
                        return Mono.error(new RefreshInstagramTokenFail());
                    }
                }).block();

        return getTokenResponseDto(response, LongLivedTokenResponse.class);
    }

    private <T> T getTokenResponseDto(String response,  Class<T> valueType) {
        try {
            return objectMapper.readValue(response,valueType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    protected List<InstagramMediaResponseDto> getMyMedias(String accessToken) {
        String targetUri = String.format("/me/media?fields=id,media_type,media_url,permalink,thumbnail_url&access_token=%s",
                accessToken);

        String response = graphClient.get()
                .uri(targetUri)
                .exchangeToMono(clientResponse -> {
                    if (clientResponse.statusCode().is2xxSuccessful()) {
                        return clientResponse.bodyToMono(String.class);
                    } else {
                        return Mono.error(new RetrieveInstagramMediaFail());
                    }
                }).block();

        try {
            Map<String, Object> map = objectMapper.readValue(response, Map.class);
            return objectMapper.convertValue(map.get("data"),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, InstagramMediaResponseDto.class));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    protected String getMyUsername(String accessToken) {
        String targetUri = String.format("/me?fields=username&access_token=%s",
                accessToken);

        String response = graphClient.get()
                .uri(targetUri)
                .exchangeToMono(clientResponse -> {
                    if (clientResponse.statusCode().is2xxSuccessful()) {
                        return clientResponse.bodyToMono(String.class);
                    } else {
                        return Mono.error(new GetInstagramUsernameFail());
                    }
                }).block();

        try {
            Map<String, Object> map = objectMapper.readValue(response, Map.class);
            return map.get("username").toString();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
