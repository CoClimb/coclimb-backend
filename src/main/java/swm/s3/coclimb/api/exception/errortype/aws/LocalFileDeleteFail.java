package swm.s3.coclimb.api.exception.errortype.aws;

import swm.s3.coclimb.api.exception.errortype.basetype.InternalServerError;

public class LocalFileDeleteFail extends InternalServerError {
    public LocalFileDeleteFail() {
        super("업로드용 로컬 파일 삭제를 실패했습니다.");
    }
}
