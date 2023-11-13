package swm.s3.coclimb.api.application.port.out.filestore;

import swm.s3.coclimb.api.application.port.out.filedownload.DownloadedFileDetail;

public interface FileStoreUpdatePort {

    void deleteFile(String fileUrl);

}
