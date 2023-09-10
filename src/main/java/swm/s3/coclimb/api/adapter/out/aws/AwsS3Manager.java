package swm.s3.coclimb.api.adapter.out.aws;

import com.amazonaws.services.s3.AmazonS3Client;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import swm.s3.coclimb.api.application.port.out.aws.AwsS3UploadPort;
import swm.s3.coclimb.api.application.port.out.filedownload.DownloadedFileDetail;
import swm.s3.coclimb.api.exception.errortype.aws.LocalFileDeleteFail;
import swm.s3.coclimb.api.exception.errortype.aws.S3UploadFail;

import java.io.File;

@Component
@RequiredArgsConstructor
public class AwsS3Manager implements AwsS3UploadPort {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    @Value("${cloud.aws.cloud_front.host}")
    private String cloudFrontHost;

    @Override
    public String uploadFile(DownloadedFileDetail fileDetail) {
        File uploadFile = null;

        try {
            uploadFile = new File(fileDetail.getPath());
            amazonS3Client.putObject(bucket, fileDetail.getName(), uploadFile);
            return cloudFrontHost + fileDetail.getName();
        } catch (Exception e) {
            throw new S3UploadFail();
        } finally {
            removeFile(uploadFile);
        }
    }

    private void removeFile(File targetFile) {
        try {
            targetFile.delete();
        } catch (Exception e) {
            throw new LocalFileDeleteFail();
        }
    }
}
