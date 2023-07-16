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

import java.time.LocalDate;
import java.time.Period;

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
            throw new RuntimeException("유효하지 않은 인증 코드");
        }

        InstaUserData instaUserData  = getShortLivedAccessTokenAndUserId(code);
        User user = userLoadPort.findByInstaUserId(instaUserData.getUserId());
        LocalDate nowDate = LocalDate.now();

        if(user == null) {
            String longLivedAccessToken = getLongLivedAccessToken(instaUserData.getShortLivedAccessToken());
            userUpdatePort.save(User.builder()
                    .instaUserId(instaUserData.getUserId())
                    .instaAccessToken(longLivedAccessToken)
                    .instaTokenExpireDate(nowDate.plusDays(60))
                    .build());
        } else {
            Period gap = Period.between(nowDate, user.getInstaTokenExpireDate());

            if(gap.getMonths() <= 0) {
                String longLivedAccessToken = null;

                if(gap.getDays() > 0) {
                    longLivedAccessToken = refreshLongLivedToken(user.getInstaAccessToken());
                } else {
                    longLivedAccessToken = getLongLivedAccessToken(instaUserData.getShortLivedAccessToken());
                }

                user.update(User.builder()
                        .instaAccessToken(longLivedAccessToken)
                        .instaTokenExpireDate(nowDate.plusDays(60))
                        .build());
            }
        }
    }

    private InstaUserData getShortLivedAccessTokenAndUserId(String code) {
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

        return new InstaUserData(
                userInfo.getString("access_token"),
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
                        return Mono.error(new RuntimeException("장기 토큰 발급 실패"));
                    }
                }).block();

        JSONObject tokenInfo = new JSONObject(response);

        return tokenInfo.getString("access_token");
    }

    private String refreshLongLivedToken(String longLivedAccessToken) {
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

        JSONObject tokenInfo = new JSONObject(response);

        return tokenInfo.getString("access_token");
    }

    @Getter
    private class InstaUserData {
        private String shortLivedAccessToken;
        private Long userId;

        public InstaUserData(String shortLivedAccessToken, Long userId) {
            this.shortLivedAccessToken = shortLivedAccessToken;
            this.userId = userId;
        }
    }
}
