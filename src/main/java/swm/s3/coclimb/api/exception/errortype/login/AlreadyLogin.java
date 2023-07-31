package swm.s3.coclimb.api.exception.errortype.login;

import swm.s3.coclimb.api.exception.errortype.basetype.Conflict;

public class AlreadyLogin extends Conflict {
    public AlreadyLogin() {
        super("이미 로그인 되어있습니다.");
    }
}
