package swm.s3.coclimb.api.application.port.in.report.dto;


import lombok.Builder;
import lombok.Getter;
import swm.s3.coclimb.domain.report.Report;
import swm.s3.coclimb.domain.user.User;

@Getter
public class ReportCreateRequestDto {
    User user;
    String subject;
    String description;

    @Builder
    public ReportCreateRequestDto(User user, String subject, String description) {
        this.user = user;
        this.subject = subject;
        this.description = description;
    }

    public Report toEntity() {
        return Report.builder()
                .user(user)
                .subject(subject)
                .description(description)
                .build();
    }
}
