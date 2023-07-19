package swm.s3.coclimb.api.auth.instagram.dto;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ShortLivedTokenResponseDto {
    String shortLivedAccessToken;
    Long userId;
}
