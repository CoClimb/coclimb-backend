package swm.s3.coclimb.api.adapter.out.persistence.user;

import org.springframework.data.jpa.repository.JpaRepository;
import swm.s3.coclimb.domain.user.User;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<User, Long> {

    Optional<User> findByInstagramUserInfoId(Long instagramUserId);
}
