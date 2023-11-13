package swm.s3.coclimb.api.adapter.out.filestore;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.model.AssumeRoleRequest;
import software.amazon.awssdk.services.sts.model.Credentials;
import swm.s3.coclimb.config.propeties.AwsS3MediaProperties;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AwsSTSManager{

    private final StsClient stsClient;
    private final AwsS3MediaProperties mediaProperties;

    public Credentials getCredentials(String bucket, String key, String action) {
        String policy = generatePolicy(bucket, key, action);
        return stsClient.assumeRole(getAssumeRoleRequest(
                        mediaProperties.getWriteRoleArn(),
                        "temp-upload-session",
                        mediaProperties.getWriteValidTime(),
                        policy))
                .credentials();
    }

    private AssumeRoleRequest getAssumeRoleRequest(String roleArn, String roleSessionName, Integer validTime, String policy) {
        AssumeRoleRequest assumeRoleRequest = AssumeRoleRequest.builder()
                .roleArn(roleArn)
                .roleSessionName(roleSessionName)
                .durationSeconds(validTime) // AWS Default minimum value = 900sec
                .policy(policy)
                .build();
        return assumeRoleRequest;
    }

    public String generateKey(String prefix, Long userId) {
        return String.format("%s/%s/%s", prefix, userId.toString(), UUID.randomUUID());
    }

    private String generatePolicy(String bucket, String key, String action) {
        String resourceArn = String.format("arn:aws:s3:::%s/%s", bucket, key);
        String policy = "{\n" +
                "    \"Version\": \"2012-10-17\",\n" +
                "    \"Statement\": [\n" +
                "        {\n" +
                "            \"Effect\": \"Allow\",\n" +
                "            \"Action\": [\n" +
                "                \"s3:"+action+"\"\n" +
                "            ],\n" +
                "            \"Resource\": \""+resourceArn+"\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";

        return policy;
    }

}
