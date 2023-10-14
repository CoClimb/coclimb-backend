package swm.s3.coclimb.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import software.amazon.awssdk.services.sts.StsClient;
import swm.s3.coclimb.api.adapter.out.aws.AwsCloudFrontManager;
import swm.s3.coclimb.config.propeties.AwsCloudFrontProperties;
import swm.s3.coclimb.config.propeties.AwsCredentialsProperties;
import swm.s3.coclimb.config.propeties.AwsRdsProperties;

import javax.sql.DataSource;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Configuration
@RequiredArgsConstructor
public class AwsConfig {

    private final AwsCredentialsProperties credentialsProperties;
    private final AwsRdsProperties rdsProperties;
    private final AwsCloudFrontProperties cloudFrontProperties;

    @Value("${cloud.aws.region.static}")
    String region;

    @Bean
    public AmazonS3Client amazonS3Client() {
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(credentialsProperties.getAccessKey(), credentialsProperties.getSecretKey());
        return (AmazonS3Client) AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withRegion(region)
                .build();
    }

    @Bean
    public StsClient StsClient(){
        return StsClient.builder()
                .credentialsProvider(() -> AwsBasicCredentials
                        .create(credentialsProperties.getAccessKey(),
                                credentialsProperties.getSecretKey()))
                .region(Region.of(region))
                .build();
    }

    @Bean
    @Profile("release")
    public DataSource dataSource() {
        RdsSecret secret = getRdsSecret();
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(rdsProperties.getDbUrl());
        dataSource.setUsername(secret.getUsername());
        dataSource.setPassword(secret.getPassword());
        return dataSource;
    }

    @Getter
    @NoArgsConstructor
    public class RdsSecret {
        private String username;
        private String password;
    }

    public RdsSecret getRdsSecret() {
        // Create a Secrets Manager client
        SecretsManagerClient client = SecretsManagerClient.builder()
                .credentialsProvider(() -> AwsBasicCredentials
                        .create(credentialsProperties.getAccessKey(),
                                credentialsProperties.getSecretKey()))
                .region(rdsProperties.getRegion())
                .build();

        GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder()
                .secretId(rdsProperties.getSecretName())
                .build();

        GetSecretValueResponse getSecretValueResponse;

        try {
            getSecretValueResponse = client.getSecretValue(getSecretValueRequest);
        } catch (Exception e) {
            // For a list of exceptions thrown, see
            // https://docs.aws.amazon.com/secretsmanager/latest/apireference/API_GetSecretValue.html
            throw e;
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(getSecretValueResponse.secretString(), RdsSecret.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    public AwsCloudFrontManager awsCloudFrontManager() {
        return new AwsCloudFrontManager(cloudFrontProperties.getHost(),getPrivateKeyFromSecret(), cloudFrontProperties.getPublicKeyId());
    }

    private PrivateKey getPrivateKeyFromSecret() {
        // [Simplified method by removing all the prints and some unnecessary variables.]
        SecretsManagerClient client = SecretsManagerClient.builder()
                .credentialsProvider(() -> AwsBasicCredentials
                        .create(credentialsProperties.getAccessKey(),
                                credentialsProperties.getSecretKey()))
                .region(Region.of(cloudFrontProperties.getRegion()))
                .build();
        GetSecretValueResponse response = client.getSecretValue(
                GetSecretValueRequest.builder().secretId(cloudFrontProperties.getSecretName()).build());
        return convertStringToPrivateKey(extractPrivateKeyFromSecret(response.secretString()));
    }

    private PrivateKey convertStringToPrivateKey(String keyContent){
        // 1. Remove headers and footers from the PEM string
        String privateKeyPEM = keyContent
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        // 2. Base64 decode to get bytes
        byte[] encoded = Base64.getDecoder().decode(privateKeyPEM);

        // 3. Create PKCS8EncodedKeySpec object from bytes
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);

        // 4. Get PrivateKey from KeyFactory
        KeyFactory keyFactory = null; // Assuming RSA. Change if needed.
        try {
            keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String extractPrivateKeyFromSecret(String secret) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(secret);
            return rootNode.get("coclimb-media-bucket-prikey").asText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract private key from secret", e);
        }
    }

}
