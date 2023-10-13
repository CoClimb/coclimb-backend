package swm.s3.coclimb.config.propeties;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "aws-config.s3.media")
public class AwsS3MediaProperties {
    private final String writeRoleArn;
    private final Integer writeValidTime;
    private final String readRoleArn;
    private final Integer readValidTime;

    public AwsS3MediaProperties(String writeRoleArn, Integer writeValidTime, String readRoleArn, Integer readValidTime) {
        this.writeRoleArn = writeRoleArn;
        this.writeValidTime = writeValidTime;
        this.readRoleArn = readRoleArn;
        this.readValidTime = readValidTime;
    }
}
