package swm.s3.coclimb.api.application.port.in.gym.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import swm.s3.coclimb.domain.gym.Gym;
import swm.s3.coclimb.domain.gym.Location;

@Getter
@NoArgsConstructor
public class GymLocationResponseDto {
    private String name;
    private Location location;

    @Builder
    public GymLocationResponseDto(String name, Location location) {
        this.name = name;
        this.location = location;
    }

    public static GymLocationResponseDto of(Gym gym) {
        return GymLocationResponseDto.builder()
                .name(gym.getName())
                .location(gym.getLocation())
                .build();
    }
}
