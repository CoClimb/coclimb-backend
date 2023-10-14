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
    private String mediaType;
    private String platform;
    @Length(max = 2048)
    private String mediaUrl;
    @Length(max = 2048)
    private String thumbnailUrl;
    @Length(max = 1024)
    private String description;

    @Embedded
    private InstagramMediaInfo instagramMediaInfo;

    @Embedded
    private MediaProblemInfo mediaProblemInfo;

    @Builder
    public Media(User user, String mediaType, String platform, String mediaUrl, String thumbnailUrl, String description, InstagramMediaInfo instagramMediaInfo, MediaProblemInfo mediaProblemInfo) {
        this.user = user;
        this.mediaType = mediaType;
        this.platform = platform;
        this.mediaUrl = mediaUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.description = description;
        this.instagramMediaInfo = instagramMediaInfo;
        this.mediaProblemInfo = mediaProblemInfo;
    }

    public void update(String description) {
        this.description = description;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }
}
