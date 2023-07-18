package swm.s3.coclimb.api.auth;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SessionUser {
    Long UserId;
    Long instaUserId;
    String instaAccessToken;
}
