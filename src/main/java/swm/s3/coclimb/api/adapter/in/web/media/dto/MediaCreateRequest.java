package swm.s3.coclimb.api.adapter.in.web.media.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import swm.s3.coclimb.api.application.port.in.media.dto.MediaCreateRequestDto;
import swm.s3.coclimb.domain.media.InstagramMediaInfo;
import swm.s3.coclimb.domain.media.MediaProblemInfo;
import swm.s3.coclimb.domain.user.User;

@Getter
@NoArgsConstructor
public class MediaCreateRequest {

    @NotNull
    String platform;
    @NotNull
    String mediaUrl;
    @NotNull
    String mediaType;
    String thumbnailUrl;
    String description;

    MediaCreateInstagramInfo instagram;
    @NotNull
    MediaCreateProblemInfo problem;

    @Builder
    public MediaCreateRequest(String platform, String mediaUrl, String mediaType, String thumbnailUrl, MediaCreateInstagramInfo instagram, MediaCreateProblemInfo problem, String description) {
        this.platform = platform;
        this.mediaUrl = mediaUrl;
        this.mediaType = mediaType;
        this.thumbnailUrl = thumbnailUrl;
        this.instagram = instagram;
        this.problem = problem;
        this.description = description;
    }

    public MediaCreateRequestDto toServiceDto(User user) {
        return MediaCreateRequestDto.builder()
                .user(user)
                .mediaUrl(mediaUrl)
                .mediaType(mediaType)
                .thumbnailUrl(thumbnailUrl)
                .platform(platform)
                .description(description)
                .instagramMediaInfo(instagram == null ? null :
                        InstagramMediaInfo.builder()
                        .id(instagram.getMediaId())
                        .userId(user.getInstagramUserInfo().getId())
                        .permalink(instagram.getPermalink())
                        .build())
                .mediaProblemInfo(MediaProblemInfo.builder()
                        .gymName(problem.getGymName())
                        .color(problem.getColor())
                        .isClear(problem.getIsClear())
                        .perceivedDifficulty(problem.getPerceivedDifficulty())
                        .type(problem.getType())
                        .clearDate(problem.getClearDate())
                        .build())
                .build();
    }
}
