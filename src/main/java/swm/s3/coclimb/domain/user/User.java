package swm.s3.coclimb.domain.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;


    @Embedded
    private InstagramUserInfo instagramUserInfo;

    @Builder
    public User(String name, InstagramUserInfo instagramUserInfo) {
        this.name = name;
        this.instagramUserInfo = instagramUserInfo;
    }

}
