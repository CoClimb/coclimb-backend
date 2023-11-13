package swm.s3.coclimb.api.exception.errortype.media;

import swm.s3.coclimb.api.exception.errortype.basetype.Unauthorized;

public class InvalidMediaUrl extends Unauthorized {
    public InvalidMediaUrl() {
        super("유효하지 않은 URL 형식입니다.");
    }
}
