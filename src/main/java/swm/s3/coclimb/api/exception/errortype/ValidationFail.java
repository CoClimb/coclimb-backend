package swm.s3.coclimb.api.exception.errortype;

import swm.s3.coclimb.api.exception.errortype.basetype.BadRequest;

public class ValidationFail extends BadRequest {
    public ValidationFail() {
        super("유효성 검사를 통과하지 못한 필드가 존재합니다.");
    }

    public ValidationFail(String message){
        super(message);
    }
    public static ValidationFail onRequest(){
        return new ValidationFail("요청에 유효하지 않은 값이 포함된 필드가 존재합니다.");
    }

    public boolean isNotEmpty() {
        return !getFields().isEmpty();
    }
}
