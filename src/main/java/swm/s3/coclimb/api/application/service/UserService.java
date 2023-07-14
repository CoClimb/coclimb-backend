package swm.s3.coclimb.api.application.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;
import swm.s3.coclimb.api.application.port.in.UserCommand;
import swm.s3.coclimb.api.application.port.out.UserLoadPort;
import swm.s3.coclimb.api.application.port.out.UserUpdatePort;
import swm.s3.coclimb.api.domain.User;
import swm.s3.coclimb.api.oauth.instagram.InstagramOAuthRecord;
import swm.s3.coclimb.api.oauth.instagram.InstagramWebClient;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService implements UserCommand {

    private final UserLoadPort userLoadPort;
    private final UserUpdatePort userUpdatePort;

    private final InstagramWebClient instagramWebClient;
    private final InstagramOAuthRecord instagramOAuthRecord;


    @Override
    @Transactional
    public void loginInstagram(String code) {
        if(code == null) {
            throw new RuntimeException("code is null");
        }

        InstaUserData instaUserData  = getAccessToken(code);
        User user = userLoadPort.findByInstaUserId(instaUserData.getUserId());

        if(user == null) {
            userUpdatePort.save(User.builder()
                    .username(null)
                    .instaUserId(instaUserData.getUserId())
                    .instaAccessToken(instaUserData.getLongLivedAccessToken())
                    .build());
        } else {
            user.update(User.builder()
                    .instaAccessToken(instaUserData.getLongLivedAccessToken())
                    .build());
        }
    }

    private InstaUserData getAccessToken(String code) {
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

        JSONObject userInfo = new JSONObject(response);
        String longLivedAccessToken = getLongLivedAccessToken(userInfo.getString("access_token"));

        return new InstaUserData(
                userInfo.getString("access_token"),
                longLivedAccessToken,
                userInfo.getLong("user_id"));
    }

    private String getLongLivedAccessToken(String shortLivedAccessToken) {
        String targetUri = String.format("/access_token?grant_type=ig_exchange_token&client_secret=%s&access_token=%s",
                instagramOAuthRecord.clientSecret(), shortLivedAccessToken);


        String response = instagramWebClient.graphClient().get()
                .uri(targetUri)
                .exchangeToMono(clientResponse -> {
                    if (clientResponse.statusCode().is2xxSuccessful()) {
                        return clientResponse.bodyToMono(String.class);
                    } else {
                        clientResponse.bodyToMono(String.class).subscribe(System.out::println);
                        return Mono.error(new RuntimeException("장기 토큰 발급 실패"));
                    }
                }).block();

        JSONObject tokenInfo = new JSONObject(response);

        return tokenInfo.getString("access_token");
    }

    @Getter
    private class InstaUserData {
        private String shortLivedAccessToken;
        private String longLivedAccessToken;
        private Long userId;

        public InstaUserData(String shortLivedAccessToken, String longLivedAccessToken, Long userId) {
            this.shortLivedAccessToken = shortLivedAccessToken;
            this.longLivedAccessToken = longLivedAccessToken;
            this.userId = userId;
        }
    }
}
