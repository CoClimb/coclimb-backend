package swm.s3.coclimb.api.exception.errortype.aws;

import swm.s3.coclimb.api.exception.errortype.basetype.InternalServerError;

public class S3DeleteFail extends InternalServerError {
    public S3DeleteFail() {
        super("S3 파일 삭제에 실패했습니다.");
    }

    public S3DeleteFail(String message) {
        super(message);
    }
}
