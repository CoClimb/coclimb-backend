package swm.s3.coclimb.api.exception.errortype.user;

import swm.s3.coclimb.api.exception.errortype.basetype.Conflict;

public class UserAlreadyExists extends Conflict {
    public UserAlreadyExists() {
        super("해당 사용자가 이미 존재합니다.");
    }
}
