package swm.s3.coclimb.domain.media;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
@Getter
public class InstagramMediaInfo {
    @Column(name = "instagram_media_id")
    private String id; // mediaId vs Id
    @Column(name = "instagram_user_id")
    private String userId;
    @Column(name = "instagram_permalink")
    private String permalink;

    @Builder
    public InstagramMediaInfo(String userId, String id, String permalink) {
        this.userId = userId;
        this.id = id;
        this.permalink = permalink;
    }
}
