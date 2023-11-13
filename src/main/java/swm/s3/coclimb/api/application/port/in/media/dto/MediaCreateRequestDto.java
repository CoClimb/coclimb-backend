package swm.s3.coclimb.api.application.port.in.media.dto;

import lombok.Builder;
import lombok.Getter;
import swm.s3.coclimb.domain.media.InstagramMediaInfo;
import swm.s3.coclimb.domain.media.Media;
import swm.s3.coclimb.domain.media.MediaProblemInfo;
import swm.s3.coclimb.domain.user.User;

@Getter
public class MediaCreateRequestDto {
    User user;
    String username;
    String platform;
    String videoUrl;
    String thumbnailUrl;
    String description;

    InstagramMediaInfo instagramMediaInfo;
    MediaProblemInfo mediaProblemInfo;

    @Builder
    public MediaCreateRequestDto(User user, String username, String platform, String videoUrl, String thumbnailUrl, InstagramMediaInfo instagramMediaInfo, MediaProblemInfo mediaProblemInfo, String description) {
        this.user = user;
        this.username = username;
        this.platform = platform;
        this.videoUrl = videoUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.description = description;
        this.instagramMediaInfo = instagramMediaInfo;
        this.mediaProblemInfo = mediaProblemInfo;
    }

    public Media toEntity(String videoKey, String thumbnailKey) {
        return Media.builder()
                .user(user)
                .videoKey(videoKey)
                .thumbnailKey(thumbnailKey)
                .instagramMediaInfo(instagramMediaInfo)
                .mediaProblemInfo(mediaProblemInfo)
                .description(description)
                .build();
    }
}
