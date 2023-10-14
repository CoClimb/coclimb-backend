package swm.s3.coclimb.api.adapter.out.aws;

import software.amazon.awssdk.services.cloudfront.CloudFrontUtilities;
import software.amazon.awssdk.services.cloudfront.model.CustomSignerRequest;
import software.amazon.awssdk.services.cloudfront.url.SignedUrl;

import java.security.PrivateKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;


public class AwsCloudFrontManager {

    private final String host;
    private final PrivateKey privateKey;
    private final String publicKeyId;
    private final CloudFrontUtilities cloudFrontUtilities;

    public AwsCloudFrontManager(String host, PrivateKey privateKey, String publicKeyId) {
        this.host = host;
        this.privateKey = privateKey;
        this.publicKeyId = publicKeyId;
        cloudFrontUtilities = CloudFrontUtilities.create();
    }

    public SignedUrl getSignedUrl (String mediaUrl) {
        return cloudFrontUtilities.getSignedUrlWithCustomPolicy(createRequestForCustomPolicy(mediaUrl));
    }

    private CustomSignerRequest createRequestForCustomPolicy(String mediaUrl) {
        return CustomSignerRequest.builder()
                .resourceUrl(mediaUrl)
                .privateKey(privateKey)
                .keyPairId(publicKeyId)
                .expirationDate(Instant.now().plus(1, ChronoUnit.DAYS))
                .build();
    }

    public String getCloudFrontUrl(String url) {
        return host+"/"+ url;
    }
}
