package swm.s3.coclimb.api.exception.errortype.instagram;

import swm.s3.coclimb.api.exception.errortype.basetype.InternalServerError;

public class IssueInstagramShortLivedTokenFail extends InternalServerError {
    public IssueInstagramShortLivedTokenFail() {
        super("단기 토큰 발급 실패");
    }
}
