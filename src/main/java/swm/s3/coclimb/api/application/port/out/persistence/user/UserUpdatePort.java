package swm.s3.coclimb.api.application.port.out.persistence.user;

import swm.s3.coclimb.domain.user.User;

public interface UserUpdatePort {
    Long save(User user);
}
