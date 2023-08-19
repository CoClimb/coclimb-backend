package swm.s3.coclimb.api.adapter.in.web.media.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MediaCreateInstagramInfo {
    String mediaId;
    String permalink;

    @Builder
    public MediaCreateInstagramInfo(String mediaId, String permalink) {
        this.mediaId = mediaId;
        this.permalink = permalink;
    }
}
