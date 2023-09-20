package swm.s3.coclimb.api.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swm.s3.coclimb.api.application.port.in.report.ReportCommand;
import swm.s3.coclimb.api.application.port.in.report.ReportQuery;
import swm.s3.coclimb.api.application.port.in.report.dto.ReportCreateRequestDto;
import swm.s3.coclimb.api.application.port.out.persistence.report.ReportLoadPort;
import swm.s3.coclimb.api.application.port.out.persistence.report.ReportUpdatePort;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReportService implements ReportCommand, ReportQuery {

    private final ReportUpdatePort reportUpdatePort;
    private final ReportLoadPort reportLoadPort;

    @Override
    @Transactional
    public void createReport(ReportCreateRequestDto request) {
        reportUpdatePort.save(request.toEntity());
    }
}
