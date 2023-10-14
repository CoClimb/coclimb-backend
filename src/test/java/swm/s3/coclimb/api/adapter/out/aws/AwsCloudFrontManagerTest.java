package swm.s3.coclimb.api.adapter.out.aws;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.cloudfront.url.SignedUrl;
import swm.s3.coclimb.api.IntegrationTestSupport;

import static org.assertj.core.api.Assertions.assertThat;

class AwsCloudFrontManagerTest extends IntegrationTestSupport {

    @Test
    @DisplayName("mediaUrl 제공 시, 콘텐츠에 접근 가능한 signedUrl을 획득한다.")
    void getSignedUrl() throws Exception {
        // given

        String mediaUrl = "https://cloudfronthost/s3bucket/key";
        // when
        SignedUrl sut = awsCloudFrontManager.getSignedUrl(mediaUrl);
        // then
        assertThat(sut).isNotNull();
        assertThat(sut.domain()).isEqualTo("cloudfronthost");
        assertThat(sut.url()).containsPattern("Policy");
        assertThat(sut.url()).containsPattern("Signature");
        assertThat(sut.url()).containsPattern("Key-Pair-Id");
    }
}