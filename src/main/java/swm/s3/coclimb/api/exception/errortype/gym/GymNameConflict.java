package swm.s3.coclimb.api.exception.errortype.gym;

import swm.s3.coclimb.api.exception.FieldErrorType;
import swm.s3.coclimb.api.exception.errortype.basetype.Conflict;

public class GymNameConflict extends Conflict {
    public GymNameConflict() {
        super("해당 이름의 암장이 이미 존재합니다.");
        addField("name", FieldErrorType.DUPLICATED);
    }
}
