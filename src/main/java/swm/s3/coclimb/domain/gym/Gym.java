package swm.s3.coclimb.domain.gym;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import swm.s3.coclimb.domain.BaseTimeEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "gyms")
public class Gym extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String address;
    private String phone;
    private String image;
    private String instagramUrl;
    private String homepageUrl;
    private String gradingSystem;

    @Embedded
    private Location location;

    @Builder
    public Gym(String name, String address, String phone, String image, String instagramUrl, String homepageUrl, String gradingSystem, Location location) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.image = image;
        this.instagramUrl = instagramUrl;
        this.homepageUrl = homepageUrl;
        this.gradingSystem = gradingSystem;
        this.location = location;
    }

    public void remove() {
        this.address = null;
        this.phone = null;
        this.image = null;
        this.instagramUrl = null;
        this.homepageUrl = null;
        this.gradingSystem = null;
        this.location = null;
    }

    public void update(Gym updateInfo) {
        this.name = (updateInfo.name == null) ? name : updateInfo.name;
        this.address = (updateInfo.address == null) ? address : updateInfo.address;
        this.phone = (updateInfo.phone == null) ? phone : updateInfo.phone;
        this.image = (updateInfo.image == null) ? image : updateInfo.image;
        this.instagramUrl = (updateInfo.instagramUrl == null) ? instagramUrl : updateInfo.instagramUrl;
        this.homepageUrl = (updateInfo.homepageUrl == null) ? homepageUrl : updateInfo.homepageUrl;
        this.gradingSystem = (updateInfo.gradingSystem == null) ? gradingSystem : updateInfo.gradingSystem;
        this.location = (updateInfo.location == null) ? location : updateInfo.location;
    }

}
