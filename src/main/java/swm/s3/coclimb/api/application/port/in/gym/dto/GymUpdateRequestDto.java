package swm.s3.coclimb.api.application.port.in.gym;

import lombok.Builder;
import lombok.Getter;
import swm.s3.coclimb.domain.Gym;

@Builder
@Getter
public class GymUpdateRequestDto {

    private String targetName;
    private Gym updateInfo;
}
