package swm.s3.coclimb.api.exception.errortype.aws;

import swm.s3.coclimb.api.exception.errortype.basetype.InternalServerError;

public class S3UploadFail extends InternalServerError {
    public S3UploadFail() {
        super("S3 파일 업로드에 실패했습니다.");
    }
}
