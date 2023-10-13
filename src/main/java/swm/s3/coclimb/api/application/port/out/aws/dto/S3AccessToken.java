package swm.s3.coclimb.api.application.port.out.aws.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.services.sts.model.Credentials;

@NoArgsConstructor
@Getter
public class S3AccessToken {
    String bucket;
    String key;
    Credentials credentials;

    @Builder
    public S3AccessToken(String bucket, String key, Credentials credentials) {
        this.bucket = bucket;
        this.key = key;
        this.credentials = credentials;
    }

    public static S3AccessToken of(String bucket, String key, Credentials credentials) {
        return S3AccessToken.builder()
                .bucket(bucket)
                .key(key)
                .credentials(credentials)
                .build();
    }
}
