package swm.s3.coclimb.api.adapter.in.web.gym.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import swm.s3.coclimb.api.application.port.in.gym.dto.GymUpdateRequestDto;
import swm.s3.coclimb.domain.gym.Gym;
import swm.s3.coclimb.domain.gym.Location;

@Getter
@NoArgsConstructor
public class GymUpdateRequest {
    @NotBlank
    private String name;
    private String updateName;
    private String updateAddress;
    private String updatePhone;
    private Location updateLocation;

    @Builder
    public GymUpdateRequest(String name, String updateName, String updateAddress, String updatePhone, Location updateLocation) {
        this.name = name;
        this.updateName = updateName;
        this.updateAddress = updateAddress;
        this.updatePhone = updatePhone;
        this.updateLocation = updateLocation;
    }

    public GymUpdateRequestDto toServiceDto() {
        return GymUpdateRequestDto.builder()
                .targetName(name)
                .updateInfo(Gym.builder()
                        .name(updateName)
                        .address(updateAddress)
                        .phone(updatePhone)
                        .location(updateLocation)
                        .build())
                .build();

    }
}
