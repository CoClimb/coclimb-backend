package swm.s3.coclimb.api.adapter.in.web.report.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import swm.s3.coclimb.api.application.port.in.report.dto.ReportCreateRequestDto;
import swm.s3.coclimb.domain.user.User;

@Getter
@NoArgsConstructor
public class ReportCreateRequest {
    @NotBlank
    private String subject;
    @NotBlank
    private String description;

    @Builder
    public ReportCreateRequest(String subject, String description) {
        this.subject = subject;
        this.description = description;
    }

    public ReportCreateRequestDto toServiceDto(User user) {
        return ReportCreateRequestDto.builder()
                .user(user)
                .subject(subject)
                .description(description)
                .build();
    }
}
