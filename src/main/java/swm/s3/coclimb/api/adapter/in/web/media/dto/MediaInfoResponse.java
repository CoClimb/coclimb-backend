package swm.s3.coclimb.api.adapter.in.web.media.dto;

import lombok.Builder;
import lombok.Getter;
import swm.s3.coclimb.domain.media.Media;
import swm.s3.coclimb.domain.media.MediaProblemInfo;

@Getter
public class MediaInfoResponse {
    Long mediaId;
    String username;
    String mediaType;
    String platform;
    String mediaUrl;
    String thumbnailUrl;
    String description;

    MediaProblemInfo problem;
    InstagramInfo instagram;

    @Getter
    @Builder
    private static class InstagramInfo {
        String permalink;
    }

    @Builder
    private MediaInfoResponse(Long mediaId, String username, String mediaType, String platform, String mediaUrl, String thumbnailUrl, String description, MediaProblemInfo problem, InstagramInfo instagram) {
        this.mediaId = mediaId;
        this.username = username;
        this.mediaType = mediaType;
        this.platform = platform;
        this.mediaUrl = mediaUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.description = description;
        this.problem = problem;
        this.instagram = instagram;
    }

    public static MediaInfoResponse of(Media media) {
        return MediaInfoResponse.builder()
                .mediaId(media.getId())
                .username(media.getUser().getName())
                .mediaType(media.getMediaType())
                .platform(media.getPlatform())
                .mediaUrl(media.getMediaUrl())
                .thumbnailUrl(media.getThumbnailUrl())
                .description(media.getDescription())
                .problem(media.getMediaProblemInfo())
                .instagram(InstagramInfo.builder().permalink(media.getInstagramMediaInfo().getPermalink()).build())
                .build();
    }
}
