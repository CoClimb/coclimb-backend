package swm.s3.coclimb.api.application.port.out.persistence.user;

import swm.s3.coclimb.domain.user.User;

import java.util.Optional;

public interface UserLoadPort {
    Optional<User> findByInstagramUserId(Long instagramUserId);

    Optional<User> findByKakaoUserId(Long kakaoUserId);

    User getById(Long id);
}
