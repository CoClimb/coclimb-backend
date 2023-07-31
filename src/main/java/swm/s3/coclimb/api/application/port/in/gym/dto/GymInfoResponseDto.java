package swm.s3.coclimb.api.application.port.in.gym.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import swm.s3.coclimb.domain.gym.Gym;

@Getter
@NoArgsConstructor
public class GymInfoResponseDto {
    private String name;
    private String address;
    private String phone;

    @Builder
    public GymInfoResponseDto(String name, String address, String phone) {
        this.name = name;
        this.address = address;
        this.phone = phone;
    }

    public static GymInfoResponseDto of(Gym gym) {
        return GymInfoResponseDto.builder()
                .name(gym.getName())
                .phone(gym.getPhone())
                .address(gym.getAddress())
                .build();
    }
}
