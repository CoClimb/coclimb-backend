package swm.s3.coclimb.api.exception.errortype.instagram;

import swm.s3.coclimb.api.exception.errortype.basetype.InternalServerError;

public class RefreshInstagramTokenFail extends InternalServerError {
    public RefreshInstagramTokenFail() {
        super("토큰 갱신 실패");
    }
}
