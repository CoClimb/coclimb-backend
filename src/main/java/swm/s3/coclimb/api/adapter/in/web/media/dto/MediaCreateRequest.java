package swm.s3.coclimb.api.adapter.in.web.media.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import swm.s3.coclimb.api.application.port.in.media.dto.MediaCreateRequestDto;
import swm.s3.coclimb.domain.media.MediaProblemInfo;
import swm.s3.coclimb.domain.user.User;

@Getter
@NoArgsConstructor
public class MediaCreateRequest {

    @NotBlank
    String videoUrl;
    @NotBlank
    String thumbnailUrl;
    String description;

    @NotNull
    MediaCreateProblemInfo problem;

    @Builder
    public MediaCreateRequest(String videoUrl, String thumbnailUrl, MediaCreateProblemInfo problem, String description) {
        this.videoUrl = videoUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.problem = problem;
        this.description = description;
    }

    public MediaCreateRequestDto toServiceDto(User user) {
        return MediaCreateRequestDto.builder()
                .user(user)
                .videoUrl(videoUrl)
                .thumbnailUrl(thumbnailUrl)
                .description(description)
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
