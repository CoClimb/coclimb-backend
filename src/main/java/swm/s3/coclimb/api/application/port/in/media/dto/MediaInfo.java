package swm.s3.coclimb.api.application.port.in.media.dto;

import lombok.Builder;
import lombok.Getter;
import swm.s3.coclimb.domain.media.Media;
import swm.s3.coclimb.domain.media.MediaProblemInfo;

@Getter
public class MediaInfo {
    Long id;
    String username;
    String videoUrl;
    String thumbnailUrl;
    String description;
    MediaProblemInfo problem;

    @Builder
    public MediaInfo(Long id, String username, String platform, String videoUrl, String thumbnailUrl, String description, MediaProblemInfo problem) {
        this.id = id;
        this.username = username;
        this.videoUrl = videoUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.description = description;
        this.problem = problem;
    }

    public static MediaInfo of(Media media, String signedVideoUrl, String signedThumbnailUrl){
        return MediaInfo.builder()
                .username(media.getUser().getName())
                .id(media.getId())
                .videoUrl(signedVideoUrl)
                .thumbnailUrl(signedThumbnailUrl)
                .problem(media.getMediaProblemInfo())
                .description(media.getDescription())
                .build();
    }
}
