package swm.s3.coclimb.api.application.port.in.gym;

import java.util.List;

public interface GymQuery {
    GymInfoResponseDto getGymInfoByName(String name);

    List<GymLocationResponseDto> getGymLocations();
}
