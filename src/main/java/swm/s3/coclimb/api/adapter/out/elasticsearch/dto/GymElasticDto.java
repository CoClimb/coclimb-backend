package swm.s3.coclimb.api.adapter.out.elasticsearch.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import swm.s3.coclimb.domain.gym.Gym;
import swm.s3.coclimb.domain.gym.Location;

@NoArgsConstructor
@Getter
public class GymElasticDto {
    private Long gym_id;
    private String name;
    private String address;
    private String phone;
    private String image_url;
    private String instagram_id;
    private String homepage_url;
    private String grading_system;
    private Float latitude;
    private Float longitude;

    @Builder
    public GymElasticDto(Long gym_id, String name, String address, String phone, String image_url, String instagram_id, String homepage_url, String grading_system, Float latitude, Float longitude) {
        this.gym_id = gym_id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.image_url = image_url;
        this.instagram_id = instagram_id;
        this.homepage_url = homepage_url;
        this.grading_system = grading_system;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Gym toDomain() {
        return Gym.builder()
                .id(gym_id)
                .name(name)
                .phone(phone)
                .address(address)
                .imageUrl(image_url)
                .instagramId(instagram_id)
                .homepageUrl(homepage_url)
                .gradingSystem(grading_system)
                .location(Location.of(latitude, longitude))
                .build();
    }

    public static GymElasticDto fromDomain(Gym gym) {
        return GymElasticDto.builder()
                .gym_id(gym.getId())
                .name(gym.getName())
                .phone(gym.getPhone())
                .address(gym.getAddress())
                .image_url(gym.getImageUrl())
                .instagram_id(gym.getInstagramId())
                .homepage_url(gym.getHomepageUrl())
                .grading_system(gym.getGradingSystem())
                .latitude(gym.getLocation() == null ? null : gym.getLocation().getLatitude())
                .longitude(gym.getLocation() == null ? null : gym.getLocation().getLongitude())
                .build();
    }
}
