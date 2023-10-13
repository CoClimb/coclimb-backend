package swm.s3.coclimb.api.application.port.out.aws;

import swm.s3.coclimb.api.application.port.out.filedownload.DownloadedFileDetail;

public interface AwsS3UpdatePort {
    String uploadFile(DownloadedFileDetail fileDetail);

    void deleteFile(String fileUrl);

    String getCloudFrontUrl(String url);
}
