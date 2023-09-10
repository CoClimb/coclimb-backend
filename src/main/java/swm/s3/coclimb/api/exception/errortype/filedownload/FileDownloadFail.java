package swm.s3.coclimb.api.exception.errortype.filedownload;

import swm.s3.coclimb.api.exception.errortype.basetype.InternalServerError;

public class FileDownloadFail extends InternalServerError {
    public FileDownloadFail() {
        super("파일 다운로드에 실패했습니다.");
    }
}
