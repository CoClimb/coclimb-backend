package swm.s3.coclimb.api.application.port.in.gym;

import lombok.Builder;
import lombok.Getter;
import swm.s3.coclimb.domain.Gym;
import swm.s3.coclimb.domain.Location;

@Getter
@Builder
public class GymCreateRequestDto {
    private String name;
    private String address;
    private String phone;
    private Location location;

    public Gym toEntity() {
        return Gym.builder()
                .name(name)
                .address(address)
                .phone(phone)
                .location(location)
                .build();
    }
}