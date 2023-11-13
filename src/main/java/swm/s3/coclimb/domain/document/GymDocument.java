package swm.s3.coclimb.domain.document;

import jakarta.persistence.Embedded;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import swm.s3.coclimb.domain.gym.Gym;
import swm.s3.coclimb.domain.gym.Location;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(indexName = "gyms")
public class GymDocument {

    @Id
    private String id;
    private String name;
    private String address;
    private String phone;
    @Length(max = 1024)
    @Field(name = "image_url", type = FieldType.Text)
    private String imageUrl;
    @Field(name = "instagram_id", type = FieldType.Text)
    private String instagramId;
    @Length(max = 1024)
    @Field(name = "homepage_url", type = FieldType.Text)
    private String homepageUrl;
    @Field(name = "grading_system", type = FieldType.Text)
    private String gradingSystem;

    @Embedded
    private Location location;

    @Builder
    public GymDocument(String id, String name, String address, String phone, String imageUrl, String instagramId, String homepageUrl, String gradingSystem, Location location) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.imageUrl = imageUrl;
        this.instagramId = instagramId;
        this.homepageUrl = homepageUrl;
        this.gradingSystem = gradingSystem;
        this.location = location;
    }

    public Gym toDomain() {
        return Gym.builder()
                .name(name)
                .phone(phone)
                .address(address)
                .imageUrl(imageUrl)
                .instagramId(instagramId)
                .homepageUrl(homepageUrl)
                .gradingSystem(gradingSystem)
                .location(location)
                .build();
    }

    public static GymDocument fromDomain(Gym gym) {
        return GymDocument.builder()
                .id(gym.getId().toString())
                .name(gym.getName())
                .phone(gym.getPhone())
                .address(gym.getAddress())
                .imageUrl(gym.getImageUrl())
                .instagramId(gym.getInstagramId())
                .homepageUrl(gym.getHomepageUrl())
                .gradingSystem(gym.getGradingSystem())
                .location(gym.getLocation())
                .build();
    }
}
