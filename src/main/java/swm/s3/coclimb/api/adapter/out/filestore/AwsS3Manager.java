package swm.s3.coclimb.api.adapter.out.filestore;

import com.amazonaws.services.s3.AmazonS3Client;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import swm.s3.coclimb.api.application.port.out.filestore.FileStoreLoadPort;
import swm.s3.coclimb.api.application.port.out.filestore.FileStoreUpdatePort;
import swm.s3.coclimb.api.application.port.out.filedownload.DownloadedFileDetail;
import swm.s3.coclimb.api.exception.errortype.aws.LocalFileDeleteFail;
import swm.s3.coclimb.api.exception.errortype.aws.S3DeleteFail;
import swm.s3.coclimb.api.exception.errortype.aws.S3UploadFail;
import swm.s3.coclimb.config.propeties.AwsCloudFrontProperties;

import java.io.File;
import java.net.URL;
import java.time.Duration;

@Component
@RequiredArgsConstructor
public class AwsS3Manager implements FileStoreUpdatePort, FileStoreLoadPort {

    private final AmazonS3Client amazonS3Client;
    private final AwsCloudFrontProperties cloudFrontProperties;
    private final S3Presigner s3Presigner;
    @Value("${aws-config.s3.bucket}")
    private String bucket;


    @Deprecated
    @Override
    public void deleteFile(String s3Key) {
        try {
            amazonS3Client.deleteObject(bucket, s3Key);
        } catch (Exception e) {
            throw new S3DeleteFail(e.getMessage());
        }
    }

    @Override
    public URL getUploadUrl(String key) {

        PutObjectPresignRequest putObjectPresignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(5))
                .putObjectRequest(PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .build())
                .build();

        return s3Presigner.presignPutObject(putObjectPresignRequest).url();
    }

}
