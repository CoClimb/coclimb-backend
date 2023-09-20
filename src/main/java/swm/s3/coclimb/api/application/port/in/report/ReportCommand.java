package swm.s3.coclimb.api.application.port.in.report;

import swm.s3.coclimb.api.application.port.in.report.dto.ReportCreateRequestDto;

public interface ReportCommand {

    void createReport(ReportCreateRequestDto request);
}
