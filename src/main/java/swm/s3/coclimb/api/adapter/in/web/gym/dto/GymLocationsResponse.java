package swm.s3.coclimb.api.adapter.in.web.gym;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import swm.s3.coclimb.api.application.port.in.gym.GymLocationResponseDto;

import java.util.List;

@Getter
@NoArgsConstructor
public class GymLocationsResponse {
    private List<GymLocationResponseDto> locations;
    private int count;
    @Builder
    public GymLocationsResponse(List<GymLocationResponseDto> locations, int count) {
        this.locations = locations;
        this.count = count;
    }

    public static GymLocationsResponse of(List<GymLocationResponseDto> locations) {
        return GymLocationsResponse.builder()
                .locations(locations)
                .count(locations.size())
                .build();
    }
}
