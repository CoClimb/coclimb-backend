package swm.s3.coclimb.api.adapter.out.instagram.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class InstagramMediaResponseDto {

    @JsonProperty("id")
    String mediaId;
    @JsonProperty("media_type")
    String mediaType;
    @JsonProperty("media_url")
    String mediaUrl;
    @JsonProperty("thumbnail_url")
    String thumbnailUrl;
    @JsonProperty("permalink")
    String permalink;

    @Builder
    public InstagramMediaResponseDto(String mediaId, String mediaType, String mediaUrl, String thumbnailUrl, String permalink) {
        this.mediaId = mediaId;
        this.mediaType = mediaType;
        this.mediaUrl = mediaUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.permalink = permalink;
    }
}
