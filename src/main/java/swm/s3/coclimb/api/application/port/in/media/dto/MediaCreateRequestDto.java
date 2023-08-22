package swm.s3.coclimb.api.application.port.in.media.dto;

import lombok.Builder;
import lombok.Getter;
import swm.s3.coclimb.domain.media.InstagramMediaInfo;
import swm.s3.coclimb.domain.media.Media;
import swm.s3.coclimb.domain.media.MediaProblemInfo;

@Getter
public class MediaCreateRequestDto {
    Long userId;
    String username;
    String platform;
    String mediaUrl;
    String mediaType;
    String thumbnailUrl;
    String description;

    InstagramMediaInfo instagramMediaInfo;
    MediaProblemInfo mediaProblemInfo;

    @Builder
    public MediaCreateRequestDto(Long userId, String username, String platform, String mediaUrl, String mediaType, String thumbnailUrl, InstagramMediaInfo instagramMediaInfo, MediaProblemInfo mediaProblemInfo, String description) {
        this.userId = userId;
        this.username = username;
        this.platform = platform;
        this.mediaUrl = mediaUrl;
        this.mediaType = mediaType;
        this.thumbnailUrl = thumbnailUrl;
        this.description = description;
        this.instagramMediaInfo = instagramMediaInfo;
        this.mediaProblemInfo = mediaProblemInfo;
    }

    public Media toEntity() {
        return Media.builder()
                .platform(platform)
                .userId(userId)
                .username(username)
                .mediaUrl(mediaUrl)
                .mediaType(mediaType)
                .thumbnailUrl(thumbnailUrl)
                .instagramMediaInfo(instagramMediaInfo)
                .mediaProblemInfo(mediaProblemInfo)
                .description(description)
                .build();
    }
}
