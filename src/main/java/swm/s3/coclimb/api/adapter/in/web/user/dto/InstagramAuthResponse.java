package swm.s3.coclimb.api.adapter.in.user.dto;

import lombok.Builder;

@Builder
public class InstagramAuthResponse {
    Long instaUserId;
    String instaAccessToken;
}
