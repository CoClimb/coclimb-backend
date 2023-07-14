package swm.s3.coclimb.api.adapter.out;

import org.springframework.data.jpa.repository.JpaRepository;
import swm.s3.coclimb.api.domain.User;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<User, Long> {
    Optional<User> findByInstaUserId(Long instaUserId);
}
