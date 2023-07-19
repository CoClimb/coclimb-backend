package swm.s3.coclimb.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import swm.s3.coclimb.api.exception.errortype.ValidationFail;

import static java.util.Objects.isNull;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Gym extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String address;
    private String phone;
    @Embedded
    private Location location;

    @Builder
    public Gym(String name, String address, String phone, Location location) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.location = location;
    }

    public void remove() {
        this.address = null;
        this.phone = null;
        this.location = null;
    }

    public void update(Gym updateInfo) {
        this.name = (updateInfo.name == null) ? name : updateInfo.name;
        this.address = (updateInfo.address == null) ? address : updateInfo.address;
        this.phone = (updateInfo.phone == null) ? phone : updateInfo.phone;
        this.location = (updateInfo.location == null) ? location : updateInfo.location;
        validate();
    }

    public void validate() {
        ValidationFail validationFail = new ValidationFail();
        if (isNull(name) || name.isBlank()) {
            validationFail.addField("name","암장 이름은 필수값입니다.");
        }

        if (validationFail.isNotEmpty()) {
            throw validationFail;
        }
    }
}
