package swm.s3.coclimb.api.adapter.in.web.gym;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import swm.s3.coclimb.api.application.port.in.gym.GymCreateRequestDto;
import swm.s3.coclimb.domain.Location;

@Getter
@NoArgsConstructor
public class GymCreateRequest {
    @NotNull(message = "암장 이름은 필수입니다.")
    private String name;
    private String address;
    private String phone;
    private Location location;
    @Builder
    public GymCreateRequest(String name, String address, String phone, Location location) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.location = location;
    }

    public GymCreateRequestDto toServiceDto() {
        return GymCreateRequestDto.builder()
                .name(name)
                .address(address)
                .phone(phone)
                .location(location)
                .build();
    }
}
