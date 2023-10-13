package swm.s3.coclimb.api.adapter.in.web.media.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.services.sts.model.Credentials;
import swm.s3.coclimb.api.application.port.out.aws.dto.S3AccessToken;

@Getter
@NoArgsConstructor
public class S3AccessTokenResponse {
    private String accessKey;
    private String secretKey;
    private String sessionToken;
    private String bucket;
    private String key;

    @Builder
    public S3AccessTokenResponse(String accessKey, String secretKey, String sessionToken, String bucket, String key) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.sessionToken = sessionToken;
        this.bucket = bucket;
        this.key = key;
    }

    public static S3AccessTokenResponse of(S3AccessToken s3AccessToken) {
        Credentials credentials = s3AccessToken.getCredentials();
        return S3AccessTokenResponse.builder()
                .accessKey(credentials.accessKeyId())
                .secretKey(credentials.secretAccessKey())
                .sessionToken(credentials.sessionToken())
                .bucket(s3AccessToken.getBucket())
                .key(s3AccessToken.getKey())
                .build();
    }

}
