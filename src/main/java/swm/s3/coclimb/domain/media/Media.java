package swm.s3.coclimb.domain.media;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import swm.s3.coclimb.domain.BaseTimeEntity;
import swm.s3.coclimb.domain.user.User;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="medias")
public class Media extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;

    @Length(max = 2048)
    private String videoKey;
    @Length(max = 2048)
    private String thumbnailKey;
    @Length(max = 1024)
    private String description;

    @Embedded
    private InstagramMediaInfo instagramMediaInfo;

    @Embedded
    private MediaProblemInfo mediaProblemInfo;

    @Builder
    public Media(User user, String videoKey, String thumbnailKey, String description, InstagramMediaInfo instagramMediaInfo, MediaProblemInfo mediaProblemInfo) {
        this.user = user;
        this.videoKey = videoKey;
        this.thumbnailKey = thumbnailKey;
        this.description = description;
        this.instagramMediaInfo = instagramMediaInfo;
        this.mediaProblemInfo = mediaProblemInfo;
    }

    public void update(String description) {
        this.description = description;
    }

}
