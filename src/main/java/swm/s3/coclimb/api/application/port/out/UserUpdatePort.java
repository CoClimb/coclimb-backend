package swm.s3.coclimb.api.application.port.out;

import swm.s3.coclimb.api.domain.User;

public interface UserUpdatePort {
    void save(User user);
}
