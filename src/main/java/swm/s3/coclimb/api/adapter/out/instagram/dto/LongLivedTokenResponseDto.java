package swm.s3.coclimb.api.auth.instagram.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LongLivedTokenResponseDto {
    String longLivedAccessToken;
    String tokenType;
    Long expiresIn;
}
