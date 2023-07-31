package swm.s3.coclimb.api.exception.errortype.gym;

import swm.s3.coclimb.api.exception.FieldErrorType;
import swm.s3.coclimb.api.exception.errortype.basetype.NotFound;

public class GymNotFound extends NotFound {
    public GymNotFound() {
        super("해당 암장을 찾을 수 없습니다.");
        addField("name", FieldErrorType.NOT_MATCH);
    }
}
