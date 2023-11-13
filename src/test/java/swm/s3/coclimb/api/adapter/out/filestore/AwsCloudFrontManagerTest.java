package swm.s3.coclimb.api.adapter.out.filestore;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.cloudfront.url.SignedUrl;
import swm.s3.coclimb.api.IntegrationTestSupport;

import static org.assertj.core.api.Assertions.assertThat;

class AwsCloudFrontManagerTest extends IntegrationTestSupport {

    @Test
    @DisplayName("s3에 저장된 콘텐츠의 key를 사용하여 해당 콘텐츠에 접근 가능한 signedUrl을 획득한다.")
    void getSignedUrl() throws Exception {
        // given

        String mediaUrl = "s3bucket/key";
        // when
        SignedUrl sut = awsCloudFrontManager.getSignedUrl(mediaUrl);
        // then
        assertThat(sut).isNotNull();
        assertThat(sut.domain()).containsPattern("cloudfront.net");
        assertThat(sut.url()).containsPattern("Policy");
        assertThat(sut.url()).containsPattern("Signature");
        assertThat(sut.url()).containsPattern("Key-Pair-Id");
    }
}