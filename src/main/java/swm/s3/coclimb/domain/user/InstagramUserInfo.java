package swm.s3.coclimb.domain.user;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InstagramUserInfo {
    @Column(name = "instagram_user_id")
    private Long id; // userId vs Id
    @Column(name = "instagram_access_token")
    private String accessToken;
    @Column(name = "instagram_token_expire_time")
    private LocalDateTime tokenExpireTime;
    @Builder
    public InstagramUserInfo(Long id, String accessToken, LocalDateTime tokenExpireTime) {
        this.id = id;
        this.accessToken = accessToken;
        this.tokenExpireTime = tokenExpireTime;
    }


    public void updateAccessToken(String token){
        this.accessToken = token;
    }

    public void updateTokenExpireTime(LocalDateTime time){
        this.tokenExpireTime = time;
    }
    public boolean isExpiredFor(LocalDateTime serverTime) {
        Duration gap = Duration.between(serverTime, tokenExpireTime);

        if(gap.getSeconds() < 30) {
            return true;
        }
        return false;
    }
    public boolean isNeedRefreshFor(LocalDateTime serverTime) {
        Duration gap = Duration.between(serverTime, tokenExpireTime);

        if(gap.toDays() < 7) {
            return true;
        }
        return false;
    }
}
