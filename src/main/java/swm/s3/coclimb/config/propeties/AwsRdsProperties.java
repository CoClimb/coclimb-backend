package swm.s3.coclimb.config.propeties;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.regions.Region;

@Getter
@Profile("release")
@ConfigurationProperties(prefix = "aws-config.rds")
public class AwsRdsProperties {
    private final String secretName;
    private final Region region;
    private final String dbUrl;


    public AwsRdsProperties(String secretName, Region region, String dbUrl) {
        this.secretName = secretName;
        this.region = region;
        this.dbUrl = dbUrl;
    }
}
