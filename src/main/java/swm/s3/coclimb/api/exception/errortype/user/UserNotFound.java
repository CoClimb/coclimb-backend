package swm.s3.coclimb.api.exception.errortype.user;

import swm.s3.coclimb.api.exception.errortype.basetype.NotFound;

public class UserNotFound extends NotFound {
    public UserNotFound() {
        super("존재하지 않는 사용자입니다.");
    }
}
