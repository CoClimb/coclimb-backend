package swm.s3.coclimb.api.adapter.in.web.report;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import swm.s3.coclimb.api.adapter.in.web.report.dto.ReportCreateRequest;
import swm.s3.coclimb.api.application.port.in.report.ReportCommand;
import swm.s3.coclimb.api.application.port.in.report.ReportQuery;
import swm.s3.coclimb.config.argumentresolver.LoginUser;
import swm.s3.coclimb.domain.user.User;

@RestController
@RequiredArgsConstructor
@Validated
public class ReportController {

    private final ReportCommand reportCommand;
    private final ReportQuery reportQuery;

    @PostMapping("/reports")
    public ResponseEntity<Void> createReport(@LoginUser User user, @RequestBody ReportCreateRequest request) {

        reportCommand.createReport(request.toServiceDto(user));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }
}
