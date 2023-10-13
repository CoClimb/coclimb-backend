package swm.s3.coclimb.domain.user;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class KakaoUserInfo {
    @Column(name = "kakao_user_id")
    private Long id;
    @Column(name = "kakao_token_type")
    private String tokenType;
    @Column(name = "kakao_access_token")
    private String accessToken;
    @Column(name = "kakao_token_expire_time")
    private LocalDateTime expireTime;
    @Column(name = "kakao_refresh_token")
    private String refreshToken;
    @Column(name = "kakao_refresh_token_expire_time")
    private LocalDateTime refreshTokenExpireTime;
    @Column(name = "kakao_scope")
    private String scope;

    @Builder
    public KakaoUserInfo(Long id, String tokenType, String accessToken, LocalDateTime expireTime, String refreshToken, LocalDateTime refreshTokenExpireTime, String scope) {
        this.id = id;
        this.tokenType = tokenType;
        this.accessToken = accessToken;
        this.expireTime = expireTime;
        this.refreshToken = refreshToken;
        this.refreshTokenExpireTime = refreshTokenExpireTime;
        this.scope = scope;
    }
}
