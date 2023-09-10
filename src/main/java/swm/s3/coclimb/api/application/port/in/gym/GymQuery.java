package swm.s3.coclimb.api.application.port.in.gym;

import org.springframework.data.domain.Page;
import swm.s3.coclimb.api.application.port.in.gym.dto.*;
import swm.s3.coclimb.domain.gym.Gym;

import java.util.List;

public interface GymQuery {
    GymInfoResponseDto getGymInfoByName(String name);

    List<GymLocationResponseDto> getGymLocations();

    Page<Gym> getPagedGyms(GymPageRequestDto request);

    List<GymNearbyResponseDto> getNearbyGyms(float latitude, float longitude, float distance);

    List<GymLikesResponseDto> getLikedGyms(Long userId);

    List<GymSearchResponseDto> searchGyms(String keyword);

    List<String> autoCorrectGymNames(String keyword, int size);
}
