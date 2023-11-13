package swm.s3.coclimb.api.adapter.in.web.media.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import swm.s3.coclimb.domain.media.Media;
import swm.s3.coclimb.domain.media.MediaProblemInfo;

@Getter
@NoArgsConstructor
public class MediaInfoResponse {
    Long id;
    String username;
    String mediaUrl;
    String thumbnailUrl;
    String description;

    MediaProblemInfo problem;

    @Builder
    private MediaInfoResponse(Long id, String username, String platform, String mediaUrl, String thumbnailUrl, String description, MediaProblemInfo problem) {
        this.id = id;
        this.username = username;
        this.mediaUrl = mediaUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.description = description;
        this.problem = problem;
    }

    public static MediaInfoResponse of(Media media) {
        return MediaInfoResponse.builder()
                .id(media.getId())
                .username(media.getUser().getName())
                .mediaUrl(media.getVideoKey())
                .thumbnailUrl(media.getThumbnailKey())
                .description(media.getDescription())
                .problem(media.getMediaProblemInfo())
                .build();
    }
}
