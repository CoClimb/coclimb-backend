package swm.s3.coclimb.api.application.service;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;
import swm.s3.coclimb.api.application.port.in.UserCommand;
import swm.s3.coclimb.api.domain.User;
import swm.s3.coclimb.api.oauth.instagram.InstagramOAuthRecord;
import swm.s3.coclimb.api.oauth.instagram.InstagramWebClient;

@Service
@RequiredArgsConstructor
public class UserSerivce implements UserCommand {

    private final InstagramWebClient instagramWebClient;
    private final InstagramOAuthRecord instagramOAuthRecord;

    @Override
    public void loginInstagram(String code) {
        if(code == null) {
            throw new RuntimeException("code is null");
        }

        UserData userData  = getShortLivedAccessToken(code);
        User user = getLongLivedAccessToken(userData.getShortLivedAccessToken());
    }

    private UserData getShortLivedAccessToken(String code) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_id", instagramOAuthRecord.clientId());
        formData.add("client_secret", instagramOAuthRecord.clientSecret());
        formData.add("grant_type", "authorization_code");
        formData.add("redirect_uri", instagramOAuthRecord.redirectUri());
        formData.add("code", code);

        String response = instagramWebClient.webClient().post()
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

        JSONObject userInfo = new JSONObject(response);

        return UserData.builder()
                .shortLivedAccessToken(userInfo.getString("access_token"))
                .userId(userInfo.getString("user_id"))
                .build();
    }

    private User getLongLivedAccessToken(String shortLivedAccessTokken) {
        String targetUri = String.format("/access_token?grant_type=ig_exchange_token&client_secret=%s&access_token=%s",
                instagramOAuthRecord.clientSecret(), shortLivedAccessTokken);

        String response = instagramWebClient.webClient().get()
                .uri(targetUri)
                .exchangeToMono(clientResponse -> {
                    if (clientResponse.statusCode().is2xxSuccessful()) {
                        return clientResponse.bodyToMono(String.class);
                    } else {
                        return Mono.error(new RuntimeException("장기 토큰 발급 실패"));
                    }
                }).block();

        JSONObject userInfo = new JSONObject(response);

        return User.builder()
                .instaUserId(userInfo.getLong("user_id"))
                .instaAccessToken(userInfo.getString("access_token"))
                .build();
    }

    @Builder
    @Getter
    private class UserData {
        private String shortLivedAccessToken;
        private String userId;
    }
}
