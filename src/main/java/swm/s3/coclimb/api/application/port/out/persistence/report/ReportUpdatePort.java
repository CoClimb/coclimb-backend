package swm.s3.coclimb.api.application.port.out.persistence.report;

import swm.s3.coclimb.domain.report.Report;

public interface ReportUpdatePort {
    void save(Report report);
}
