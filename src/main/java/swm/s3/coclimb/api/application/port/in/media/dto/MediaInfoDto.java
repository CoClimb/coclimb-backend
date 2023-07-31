package swm.s3.coclimb.api.application.port.in.media.dto;

import lombok.Builder;
import lombok.Getter;
import swm.s3.coclimb.domain.media.Media;

@Getter
public class MediaInfoDto {
    Long id;
    String username;
    String platform;
    String mediaType;
    String mediaUrl;
    String thumbnailUrl;
    String instagramPermalink;

    @Builder
    public MediaInfoDto(Long id, String platform, String mediaType, String mediaUrl, String thumbnailUrl, String instagramPermalink, String username) {
        this.id = id;
        this.platform = platform;
        this.mediaType = mediaType;
        this.mediaUrl = mediaUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.instagramPermalink = instagramPermalink;
        this.username = username;
    }

    public static MediaInfoDto of(Media media){
        return MediaInfoDto.builder()
                .platform(media.getPlatform())
                .username(media.getUsername())
                .id(media.getId())
                .mediaType(media.getMediaType())
                .mediaUrl(media.getMediaUrl())
                .thumbnailUrl(media.getThumbnailUrl())
                .instagramPermalink(media.getInstagramMediaInfo().getPermalink())
                .build();
    }
}
