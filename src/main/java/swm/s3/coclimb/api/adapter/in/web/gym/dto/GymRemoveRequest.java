package swm.s3.coclimb.api.adapter.in.web.gym;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GymRemoveRequest {
    @NotBlank(message = "정보를 제거할 암장의 이름은 필수입니다.")
    private String name;
    @Builder
    public GymRemoveRequest(String name) {
        this.name = name;
    }
}
