package swm.s3.coclimb.api.adapter.in.web.login.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {
    private String accessToken;
}
