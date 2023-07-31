package swm.s3.coclimb.api.adapter.in.web.login.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class InstagramLoginRequest {
    @NotNull
    String code;

    private InstagramLoginRequest(String code) {
        this.code = code;
    }

    public static InstagramLoginRequest of(String code) {
        return new InstagramLoginRequest(code);
    }
}
