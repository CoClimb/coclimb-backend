package swm.s3.coclimb.api.adapter.out.aws;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.sts.model.Credentials;
import swm.s3.coclimb.api.IntegrationTestSupport;
import swm.s3.coclimb.api.exception.errortype.aws.S3UploadFail;

import static org.assertj.core.api.Assertions.assertThat;

class AwsSTSManagerTest extends IntegrationTestSupport {

    @Test
    @DisplayName("특정 리소스에만 접근 가능한 임시 권한을 획득한다.")
    void getTempAuthToken() throws Exception {
        // given
        Long userId = 1L;
        String bucket = "coclimb-media-bucket";
        String prefix = "test";
        String action = "PutObject";
        String key = awsSTSManager.generateKey(prefix, userId);
        // when
        Credentials sut = awsSTSManager.getCredentials(bucket, key, action);

        // then
        assertThat(sut).isNotNull();
        Assertions.assertThatCode(() ->
                uploadToSpecificResource(bucket, key, sut)).doesNotThrowAnyException();
    }


    @Test
    @DisplayName("권한이 없는 리소스에 접근 시 예외가 발생한다.")
    void uploadByUnauthorizedToken() throws Exception {
        // given
        Long userId = 1L;
        String bucket = "coclimb-media-bucket";
        String prefix = "test";
        String key = awsSTSManager.generateKey(prefix, userId);
        String action = "PutObject";

        // when
        Credentials sut = awsSTSManager.getCredentials(bucket,key,action);

        // then
        assertThat(sut).isNotNull();
        Assertions.assertThatThrownBy(() ->
                uploadToSpecificResource(bucket, key+"unauthorized", sut)).isInstanceOf(S3UploadFail.class);
    }

}