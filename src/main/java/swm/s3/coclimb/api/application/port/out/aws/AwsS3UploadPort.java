package swm.s3.coclimb.api.application.port.out.aws;

import swm.s3.coclimb.api.application.port.out.filedownload.DownloadedFileDetail;

public interface AwsS3UploadPort {
    String uploadFile(DownloadedFileDetail fileDetail);
}
