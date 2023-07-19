package swm.s3.coclimb.api.adapter.in.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class InstagramAuthRequest {
    @NotNull(message = "인증 코드는 필수 정보입니다.")
    String code;
}
