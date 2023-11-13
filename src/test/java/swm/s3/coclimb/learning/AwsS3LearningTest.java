package swm.s3.coclimb.learning;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.model.AssumeRoleRequest;
import software.amazon.awssdk.services.sts.model.Credentials;
import swm.s3.coclimb.api.IntegrationTestSupport;
import swm.s3.coclimb.api.exception.errortype.aws.S3UploadFail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.Duration;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class AwsS3LearningTest extends IntegrationTestSupport {

    private final String ROLE_ARN = "arn:aws:iam::085796513949:role/CoclimbS3Access";
    private final String ROLE_ARN2 = "arn:aws:iam::085796513949:role/CoclimbS3ReadOnly";

    @Autowired
    StsClient stsClient;

    @Test
    @DisplayName("AWS Security Token Service를 통해 임시 접근 권한을 획득한다.")
    void getTempAuth() throws Exception {
        // given
        String bucket = "coclimb-media-bucket";
        AssumeRoleRequest assumeRoleRequest = AssumeRoleRequest.builder()
                .roleArn(ROLE_ARN)
                .roleSessionName("temp-upload-session")
                .durationSeconds(900) //최소 15분이어야함
                .build();
        Credentials credentials = stsClient.assumeRole(assumeRoleRequest).credentials();

        AssumeRoleRequest assumeRole2Request = AssumeRoleRequest.builder()
                .roleArn(ROLE_ARN2)
                .roleSessionName("temp-read-session")
                .durationSeconds(900) //최소 15분이어야함
                .build();
        Credentials credentials2 = stsClient.assumeRole(assumeRole2Request).credentials();

        // when
        uploadToS3(bucket, credentials);

        // then
        String sut = downloadFromS3(bucket, credentials2);
        Assertions.assertThat(sut).isEqualTo("test");
    }

    @Test
    @DisplayName("특정 리소스에만 접근 가능한 임시 접근 권한을 획득한다.")
    void getTempAuthForSpecificResource() throws Exception {
        // given
        String bucket = "coclimb-media-bucket";
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        AssumeRoleRequest assumeRoleRequest = AssumeRoleRequest.builder()
                .roleArn(ROLE_ARN)
                .roleSessionName("temp-upload-session")
                .durationSeconds(900) //최소 15분이어야함
                .policy(generatePolicy(bucket, "test", uuid))
                .build();

        Credentials credentials = stsClient.assumeRole(assumeRoleRequest).credentials();

        // when, then
        uploadToSpecificResource(bucket, "test/" + uuid, credentials);
    }

    @Test
    @DisplayName("권한이 없는 리소스에서 접근 시 예외가 발생한다.")
    void uploadFailToUnauthorizedResource() throws Exception {
        // given
        String bucket = "coclimb-media-bucket";
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        AssumeRoleRequest assumeRoleRequest = AssumeRoleRequest.builder()
                .roleArn(ROLE_ARN)
                .roleSessionName("temp-upload-session")
                .durationSeconds(900) //최소 15분이어야함
                .policy(generatePolicy(bucket, "test", uuid))
                .build();

        Credentials credentials = stsClient.assumeRole(assumeRoleRequest).credentials();
        // when, then
        assertThatThrownBy(() -> uploadToS3(bucket, credentials)).isInstanceOf(S3UploadFail.class);
        assertThatThrownBy(() -> uploadToSpecificResource(bucket, "test/"+uuid+"attack",credentials)).isInstanceOf(S3UploadFail.class);
        assertThatThrownBy(() -> uploadToSpecificResource(bucket, "test/"+uuid+"/attack",credentials)).isInstanceOf(S3UploadFail.class);
    }

    @Test
    @DisplayName("서명된 업로드 URL을 생성한다.")
    void preSignedUrl() throws Exception {
        // given
        String bucket = "coclimb-media-bucket";
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        URL preSignedUrl = getPreSignedUrl(bucket, "test/" + uuid);
        // when
        Integer code = uploadByPreSignedUrl(preSignedUrl);

        // then
        Assertions.assertThat(code).isEqualTo(200);
    }

    public String generatePolicy(String bucketName, String prefix, String resourceName) {
        String resourceArn = String.format("arn:aws:s3:::%s/%s/%s", bucketName, prefix, resourceName);
        //prefix : media?
        String policy = "{\n" +
                "    \"Version\": \"2012-10-17\",\n" +
                "    \"Statement\": [\n" +
                "        {\n" +
                "            \"Effect\": \"Allow\",\n" +
                "            \"Action\": [\n" +
                "                \"s3:PutObject\"\n" +
                "            ],\n" +
                "            \"Resource\": \""+resourceArn+"\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";

        return policy;
    }
    private void uploadToS3(String bucket, Credentials credentials) {

        BasicSessionCredentials awsCredentials = new BasicSessionCredentials(credentials.accessKeyId(), credentials.secretAccessKey(), credentials.sessionToken());
        AmazonS3Client amazonS3Client = (AmazonS3Client) AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withRegion("ap-northeast-2")
                .build();

        try {
            amazonS3Client.putObject(bucket, "coclimblearningtestfile", "test");
        } catch (Exception e) {
            throw new S3UploadFail();
        }
    }

    private String downloadFromS3(String bucket, Credentials credentials) {

            BasicSessionCredentials awsCredentials = new BasicSessionCredentials(credentials.accessKeyId(), credentials.secretAccessKey(), credentials.sessionToken());
            AmazonS3Client amazonS3Client = (AmazonS3Client) AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                    .withRegion("ap-northeast-2")
                    .build();

            // 파일 다운로드
        S3Object file = amazonS3Client.getObject(bucket, "coclimblearningtestfile");

        return convertStreamToString(file.getObjectContent());
    }

    private static String convertStreamToString(java.io.InputStream is) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            sb.deleteCharAt(sb.length()-1);
            return sb.toString();
        } catch (IOException e) {
            throw new RuntimeException("Error while converting InputStream to String", e);
        }
    }

    public URL getPreSignedUrl(String bucket, String key) {

        // S3 Presigner 생성 (presigned URL 생성에 사용)
        S3Presigner presigner = S3Presigner.builder()
                .credentialsProvider(ProfileCredentialsProvider.create())
                .region(Region.AP_NORTHEAST_2)
                .build();

        try {
            // Presigned URL의 만료 시간 설정 (예: 1시간)
            Duration expiration = Duration.ofHours(1);

            // GetObjectRequest 생성

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            // GetObjectPresignRequest 생성
            GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(expiration)
                    .getObjectRequest(getObjectRequest)
                    .build();
            PutObjectPresignRequest putObjectPresignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(expiration)
                    .putObjectRequest(PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .build())
                    .build();

            // Presigned URL 생성
//            PresignedGetObjectRequest presignedGetObjectRequest = presigner.presignGetObject(getObjectPresignRequest);
            PresignedPutObjectRequest presignedPutObjectRequest = presigner.presignPutObject(putObjectPresignRequest);
            // 생성된 Presigned URL 출력
            System.out.println("Presigned URL: " + presignedPutObjectRequest.url());
            return presignedPutObjectRequest.url();

        } finally {
            // Presigner 리소스 해제
            presigner.close();
        }
    }

}
