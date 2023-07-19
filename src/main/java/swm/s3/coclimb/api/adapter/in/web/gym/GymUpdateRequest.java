package swm.s3.coclimb.api.adapter.in.web.gym;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import swm.s3.coclimb.api.application.port.in.gym.GymUpdateRequestDto;
import swm.s3.coclimb.domain.Gym;
import swm.s3.coclimb.domain.Location;

import java.util.Set;

import static java.util.Objects.isNull;

@Getter
@NoArgsConstructor
public class GymUpdateRequest {
    @NotBlank(message = "수정할 암장의 이름은 필수입니다.")
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
