package swm.s3.coclimb.api.adapter.out.aws;

import com.amazonaws.services.s3.AmazonS3Client;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import swm.s3.coclimb.api.application.port.out.aws.AwsS3UpdatePort;
import swm.s3.coclimb.api.application.port.out.filedownload.DownloadedFileDetail;
import swm.s3.coclimb.api.exception.errortype.aws.LocalFileDeleteFail;
import swm.s3.coclimb.api.exception.errortype.aws.S3DeleteFail;
import swm.s3.coclimb.api.exception.errortype.aws.S3UploadFail;
import swm.s3.coclimb.config.propeties.AwsCloudFrontProperties;

import java.io.File;

@Component
@RequiredArgsConstructor
public class AwsS3Manager implements AwsS3UpdatePort {

    private final AmazonS3Client amazonS3Client;
    private final AwsCloudFrontProperties cloudFrontProperties;
    @Value("${aws-config.s3.bucket}")
    private String bucket;

    @Override
    public String uploadFile(DownloadedFileDetail fileDetail) {
        File uploadFile = null;

        try {
            uploadFile = new File(fileDetail.getPath());
            amazonS3Client.putObject(bucket, fileDetail.getName(), uploadFile);
            return cloudFrontProperties.getHost() + fileDetail.getName();
        } catch (Exception e) {
            throw new S3UploadFail();
        } finally {
            removeLocalFile(uploadFile);
        }
    }

    @Override
    public void deleteFile(String fileUrl) {
        try {
            String fileName = fileUrl.substring(cloudFrontProperties.getHost().length());
            amazonS3Client.deleteObject(bucket, fileName);
        } catch (Exception e) {
            throw new S3DeleteFail();
        }
    }

    private void removeLocalFile(File targetFile) {
        try {
            targetFile.delete();
        } catch (Exception e) {
            throw new LocalFileDeleteFail();
        }
    }

}
