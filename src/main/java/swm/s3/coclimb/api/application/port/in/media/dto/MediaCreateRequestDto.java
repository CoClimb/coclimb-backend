package swm.s3.coclimb.api.application.port.in.media.dto;

import lombok.Builder;
import lombok.Getter;
import swm.s3.coclimb.domain.media.InstagramMediaInfo;
import swm.s3.coclimb.domain.media.Media;

@Getter
public class MediaCreateRequestDto {
    Long userId;
    String username;
    String platform;
    String mediaUrl;
    String mediaType;
    String thumbnailUrl;

    String instagramMediaId;
    String instagramUserId;
    String instagramPermalink;

    @Builder
    public MediaCreateRequestDto(Long userId, String mediaUrl, String mediaType, String thumbnailUrl, String instagramMediaId, String instagramUserId, String platform, String instagramPermalink, String username) {
        this.userId = userId;
        this.username = username;
        this.mediaUrl = mediaUrl;
        this.mediaType = mediaType;
        this.thumbnailUrl = thumbnailUrl;
        this.platform = platform;
        this.instagramMediaId = instagramMediaId;
        this.instagramUserId = instagramUserId;
        this.instagramPermalink = instagramPermalink;
    }

    public Media toEntity() {
        return Media.builder()
                .platform(platform)
                .userId(userId)
                .username(username)
                .mediaUrl(mediaUrl)
                .mediaType(mediaType)
                .thumbnailUrl(thumbnailUrl)
                .instagramMediaInfo(InstagramMediaInfo.builder()
                        .id(instagramMediaId)
                        .userId(instagramUserId)
                        .permalink(instagramPermalink)
                        .build())
                .build();
    }
}
