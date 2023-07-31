package swm.s3.coclimb.api.application.port.in.gym;

import org.springframework.data.domain.Page;
import swm.s3.coclimb.api.application.port.in.gym.dto.GymInfoResponseDto;
import swm.s3.coclimb.api.application.port.in.gym.dto.GymLocationResponseDto;
import swm.s3.coclimb.api.application.port.in.gym.dto.GymPageRequestDto;
import swm.s3.coclimb.domain.gym.Gym;

import java.util.List;

public interface GymQuery {
    GymInfoResponseDto getGymInfoByName(String name);

    List<GymLocationResponseDto> getGymLocations();

    Page<Gym> getPagedGyms(GymPageRequestDto request);
}
