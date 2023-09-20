package swm.s3.coclimb.api.adapter.out.persistence.report;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import swm.s3.coclimb.api.application.port.out.persistence.report.ReportLoadPort;
import swm.s3.coclimb.api.application.port.out.persistence.report.ReportUpdatePort;
import swm.s3.coclimb.domain.report.Report;

@Repository
@RequiredArgsConstructor
public class ReportRepository implements ReportLoadPort, ReportUpdatePort {

    private final ReportJpaRepository reportJpaRepository;

    @Override
    public void save(Report report) {
        reportJpaRepository.save(report);
    }
}
