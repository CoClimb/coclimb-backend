package swm.s3.coclimb.api.adapter.in.web.login.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class InstagramLoginPageResponse {
    String loginPageUrl;

    @Builder
    public InstagramLoginPageResponse(String loginPageUrl) {
        this.loginPageUrl = loginPageUrl;
    }
}
