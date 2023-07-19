package swm.s3.coclimb.api.auth;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SessionUser {
    Long userId;
    Long instaUserId;
    String instaAccessToken;
}
