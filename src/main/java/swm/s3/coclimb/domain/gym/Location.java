package swm.s3.coclimb.domain.gym;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Location {
    private Float latitude;
    private Float longitude;

    @Builder
    public Location(Float latitude, Float longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public static Location of(Float latitude, Float longitude) {
        return Location.builder()
                .latitude(latitude)
                .longitude(longitude)
                .build();
    }

}
