package swm.s3.coclimb.api.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "TB_USER")
public class User {

    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private Long id;

    private String username;

    // instagram information
    private Long instaUserId;
    private String instaAccessToken;

    @Builder
    public User(String username, Long instaUserId, String instaAccessToken) {
        this.username = username;
        this.instaUserId = instaUserId;
        this.instaAccessToken = instaAccessToken;
    }
}
