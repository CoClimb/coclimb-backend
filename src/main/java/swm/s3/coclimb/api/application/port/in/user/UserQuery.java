package swm.s3.coclimb.api.application.port.in.user;

import swm.s3.coclimb.api.domain.User;

public interface UserQuery {
    User findById(Long id);
}
