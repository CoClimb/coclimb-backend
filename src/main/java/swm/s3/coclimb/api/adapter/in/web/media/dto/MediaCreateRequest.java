package swm.s3.coclimb.api.adapter.in.web.media.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import swm.s3.coclimb.api.application.port.in.media.dto.MediaCreateRequestDto;
import swm.s3.coclimb.domain.user.User;

@Getter
@NoArgsConstructor
public class MediaCreateRequest {

    // common attributes
    @NotNull
    String platform;
    @NotNull
    String mediaUrl;
    @NotNull
    String mediaType;
    String thumbnailUrl;

    // for instagram
    String instagramMediaId;
    String instagramPermalink;

    @Builder
    public MediaCreateRequest(String platform, String mediaUrl, String mediaType, String thumbnailUrl, String instagramMediaId, String instagramPermalink) {
        this.platform = platform;
        this.mediaUrl = mediaUrl;
        this.mediaType = mediaType;
        this.thumbnailUrl = thumbnailUrl;
        this.instagramMediaId = instagramMediaId;
        this.instagramPermalink = instagramPermalink;
    }

    public MediaCreateRequestDto toServiceDto(User user) {
        return MediaCreateRequestDto.builder()
                .userId(user.getId())
                .username(user.getName())
                .mediaUrl(mediaUrl)
                .mediaType(mediaType)
                .thumbnailUrl(thumbnailUrl)
                .platform(platform)
                .instagramMediaId(instagramMediaId)
                .instagramUserId(user.getInstagramUserInfo().getId().toString())
                .instagramPermalink(instagramPermalink)
                .build();
    }
}
