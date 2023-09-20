package swm.s3.coclimb.api.adapter.out.persistence.report;

import org.springframework.data.jpa.repository.JpaRepository;
import swm.s3.coclimb.domain.report.Report;

public interface ReportJpaRepository extends JpaRepository<Report, Long> {
}
