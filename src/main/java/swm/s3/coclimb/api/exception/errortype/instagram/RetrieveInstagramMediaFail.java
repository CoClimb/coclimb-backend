package swm.s3.coclimb.api.exception.errortype.instagram;

import swm.s3.coclimb.api.exception.errortype.basetype.InternalServerError;

public class RetrieveInstagramMediaFail extends InternalServerError {
    public RetrieveInstagramMediaFail() {
        super("인스타그램 미디어 받아오기 실패");
    }
}
