package swm.s3.coclimb.api.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDate;

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
    private LocalDate instaTokenExpireDate;

    @Builder
    public User(String username, Long instaUserId, String instaAccessToken, LocalDate instaTokenExpireDate) {
        this.username = username;
        this.instaUserId = instaUserId;
        this.instaAccessToken = instaAccessToken;
        this.instaTokenExpireDate = instaTokenExpireDate;
    }

    public void update(User updatePart) {
        this.username = (updatePart.username == null) ? this.username : updatePart.username;
        this.instaUserId = (updatePart.instaUserId == null) ? this.instaUserId : updatePart.instaUserId;
        this.instaAccessToken = (updatePart.instaAccessToken == null) ? this.instaAccessToken : updatePart.instaAccessToken;
        this.instaTokenExpireDate = (updatePart.instaTokenExpireDate == null) ? this.instaTokenExpireDate : updatePart.instaTokenExpireDate;
    }
}
