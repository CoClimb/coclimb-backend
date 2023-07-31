package swm.s3.coclimb.api.adapter.out.instagram.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ShortLivedTokenResponse {
    @JsonProperty("access_token")
    String token;
    @JsonProperty("user_id")
    Long userId;
}
