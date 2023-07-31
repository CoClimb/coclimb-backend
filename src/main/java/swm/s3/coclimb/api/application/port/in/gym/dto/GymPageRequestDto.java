package swm.s3.coclimb.api.application.port.in.gym.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GymPageRequestDto {
    private int page;
    private int size;
    private String sort;

}
