package swm.s3.coclimb.api.application.port.in.user;

import swm.s3.coclimb.domain.user.User;

import java.util.Optional;

public interface UserQuery {

    Optional<User> findUserByInstagramUserId(Long instagramUserId);
}
