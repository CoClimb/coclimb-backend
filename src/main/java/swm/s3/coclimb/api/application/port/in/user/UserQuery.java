package swm.s3.coclimb.api.application.port.in.user;

import swm.s3.coclimb.domain.User;

public interface UserQuery {
    User getUserByInstagramUserId(Long instagramUserId);
}
