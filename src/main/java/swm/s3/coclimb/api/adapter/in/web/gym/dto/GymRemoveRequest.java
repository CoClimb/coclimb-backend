package swm.s3.coclimb.api.adapter.in.web.gym.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GymRemoveRequest {
    @NotBlank
    private String name;
    @Builder
    public GymRemoveRequest(String name) {
        this.name = name;
    }
}
