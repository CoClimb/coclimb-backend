package swm.s3.coclimb.api.exception.errortype.instagram;

import swm.s3.coclimb.api.exception.FieldErrorType;
import swm.s3.coclimb.api.exception.errortype.basetype.BadRequest;

public class InvalidInstagramCodeFail extends BadRequest {
    public InvalidInstagramCodeFail() {
        super("유효하지 않은 code");
        addField("code", FieldErrorType.INVALID_VALUE);
    }
}
