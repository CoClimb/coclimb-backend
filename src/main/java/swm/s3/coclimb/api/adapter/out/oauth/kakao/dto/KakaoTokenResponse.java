package swm.s3.coclimb.api.adapter.out.oauth.kakao.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import swm.s3.coclimb.domain.user.KakaoUserInfo;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KakaoTokenResponse {
    @JsonProperty("token_type")
    String tokenType;
    @JsonProperty("access_token")
    String accessToken;
    @JsonProperty("expires_in")
    Long expiresIn;
    @JsonProperty("refresh_token")
    String refreshToken;
    @JsonProperty("refresh_token_expires_in")
    Long refreshTokenExpiresIn;
    @JsonProperty("scope")
    String scope;

    public KakaoUserInfo toKakaoUserInfoEntity(LocalDateTime now, Long id) {
        return KakaoUserInfo.builder()
                .id(id)
                .tokenType(tokenType)
                .accessToken(accessToken)
                .expireTime(now.plusSeconds(expiresIn))
                .refreshToken(refreshToken)
                .refreshTokenExpireTime(now.plusSeconds(refreshTokenExpiresIn))
                .scope(scope)
                .build();
    }
}
