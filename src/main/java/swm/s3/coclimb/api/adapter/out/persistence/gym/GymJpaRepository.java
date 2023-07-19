package swm.s3.coclimb.api.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import swm.s3.coclimb.domain.Gym;

import java.util.Optional;

public interface GymJpaRepository extends JpaRepository<Gym, Long> {

    boolean existsByName(String name);

    Optional<Gym> findByName(String name);
}
