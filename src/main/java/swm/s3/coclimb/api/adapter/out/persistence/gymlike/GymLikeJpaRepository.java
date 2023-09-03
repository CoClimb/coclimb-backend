package swm.s3.coclimb.api.adapter.out.persistence.gymlike;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import swm.s3.coclimb.domain.gymlike.GymLike;

import java.util.List;
import java.util.Optional;

public interface GymLikeJpaRepository extends JpaRepository<GymLike, Long> {

    @EntityGraph(attributePaths = {"user", "gym"})
    List<GymLike> findByUserId(Long userId);

    Optional<GymLike> findByUserIdAndGymId(Long userId, Long gymId);
}
