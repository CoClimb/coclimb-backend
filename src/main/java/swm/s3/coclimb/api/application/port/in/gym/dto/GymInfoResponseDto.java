package swm.s3.coclimb.api.application.port.in.gym.dto;

import lombok.Builder;
import lombok.Getter;
import swm.s3.coclimb.domain.gym.Gym;

@Getter
@Builder
public class GymInfoResponseDto {
    private String name;
    private String address;
    private String phone;
    private String image;
    private String instagramUrl;
    private String homepageUrl;
    private String gradingSystem;


    public static GymInfoResponseDto of(Gym gym) {
        return GymInfoResponseDto.builder()
                .name(gym.getName())
                .address(gym.getAddress())
                .phone(gym.getPhone())
                .image(gym.getImage())
                .instagramUrl(gym.getInstagramUrl())
                .homepageUrl(gym.getHomepageUrl())
                .gradingSystem(gym.getGradingSystem())
                .build();
    }

}
