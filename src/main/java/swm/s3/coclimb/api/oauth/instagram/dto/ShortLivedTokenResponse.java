package swm.s3.coclimb.api.oauth.instagram.dto;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ShortLivedTokenResponse {
    String shortLivedAccessToken;
    Long userId;
}
