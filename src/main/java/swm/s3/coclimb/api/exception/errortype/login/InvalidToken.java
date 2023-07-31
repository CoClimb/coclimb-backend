package swm.s3.coclimb.api.exception.errortype.login;

import swm.s3.coclimb.api.exception.errortype.basetype.Unauthorized;

public class InvalidToken extends Unauthorized {
    public InvalidToken() {
        super("유효하지 않은 토큰입니다.");
    }
}
