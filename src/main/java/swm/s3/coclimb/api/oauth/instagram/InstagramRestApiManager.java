package swm.s3.coclimb.api.oauth.instagram;

import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;
import swm.s3.coclimb.api.oauth.instagram.dto.LongLivedTokenResponseDto;
import swm.s3.coclimb.api.oauth.instagram.dto.ShortLivedTokenResponseDto;

@Component
@RequiredArgsConstructor
public class InstagramRestApiManager {

    private final InstagramWebClient instagramWebClient;
    private final InstagramOAuthRecord instagramOAuthRecord;

    public ShortLivedTokenResponseDto getShortLivedAccessTokenAndUserId(String code) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_id", instagramOAuthRecord.clientId());
        formData.add("client_secret", instagramOAuthRecord.clientSecret());
        formData.add("grant_type", "authorization_code");
        formData.add("redirect_uri", instagramOAuthRecord.redirectUri());
        formData.add("code", code);

        String response = instagramWebClient.basicDisplayClient().post()
                .uri("/oauth/access_token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .exchangeToMono(clientResponse -> {
                    if (clientResponse.statusCode().is2xxSuccessful()) {
                        return clientResponse.bodyToMono(String.class);
                    } else {
                        return Mono.error(new RuntimeException("단기 토큰 발급 실패"));
                    }
                }).block();

        JSONObject responseJson = new JSONObject(response);

        return ShortLivedTokenResponseDto.builder()
                .shortLivedAccessToken(responseJson.getString("access_token"))
                .userId(responseJson.getLong("user_id"))
                .build();
    }

    public LongLivedTokenResponseDto getLongLivedAccessToken(String shortLivedAccessToken) {
        String targetUri = String.format("/access_token?grant_type=ig_exchange_token&client_secret=%s&access_token=%s",
                instagramOAuthRecord.clientSecret(), shortLivedAccessToken);


        String response = instagramWebClient.graphClient().get()
                .uri(targetUri)
                .exchangeToMono(clientResponse -> {
                    if (clientResponse.statusCode().is2xxSuccessful()) {
                        return clientResponse.bodyToMono(String.class);
                    } else {
                        return Mono.error(new RuntimeException("장기 토큰 발급 실패"));
                    }
                }).block();

        JSONObject responseJson = new JSONObject(response);

        return LongLivedTokenResponseDto.builder()
                .longLivedAccessToken(responseJson.getString("access_token"))
                .tokenType(responseJson.getString("token_type"))
                .expiresIn(responseJson.getLong("expires_in"))
                .build();
    }

    public LongLivedTokenResponseDto refreshLongLivedToken(String longLivedAccessToken) {
        String targetUri = String.format("/refresh_access_token?grant_type=ig_refresh_token&access_token=%s",
                longLivedAccessToken);

        String response = instagramWebClient.graphClient().get()
                .uri(targetUri)
                .exchangeToMono(clientResponse -> {
                    if (clientResponse.statusCode().is2xxSuccessful()) {
                        return clientResponse.bodyToMono(String.class);
                    } else {
                        return Mono.error(new RuntimeException("장기 토큰 갱신 실패"));
                    }
                }).block();

        JSONObject responseJson = new JSONObject(response);

        return LongLivedTokenResponseDto.builder()
                .longLivedAccessToken(responseJson.getString("access_token"))
                .tokenType(responseJson.getString("token_type"))
                .expiresIn(responseJson.getLong("expires_in"))
                .build();
    }
}
