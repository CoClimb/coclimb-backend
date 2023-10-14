package swm.s3.coclimb.config.propeties;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "aws-config.cloudfront")
public class AwsCloudFrontProperties {

    private final String secretName;
    private final String region;
    private final String publicKeyId;
    private final String host;

    public AwsCloudFrontProperties(String secretName, String region, String publicKeyId, String host) {
        this.secretName = secretName;
        this.region = region;
        this.publicKeyId = publicKeyId;
        this.host = host;
    }

}
