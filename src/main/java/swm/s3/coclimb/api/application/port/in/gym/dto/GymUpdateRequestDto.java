package swm.s3.coclimb.api.application.port.in.gym.dto;

import lombok.Builder;
import lombok.Getter;
import swm.s3.coclimb.domain.gym.Gym;

@Builder
@Getter
public class GymUpdateRequestDto {

    private String targetName;
    private Gym updateInfo;
}
