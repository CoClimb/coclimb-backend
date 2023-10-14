package swm.s3.coclimb.learning;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudfront.CloudFrontUtilities;
import software.amazon.awssdk.services.cloudfront.model.CannedSignerRequest;
import software.amazon.awssdk.services.cloudfront.model.CustomSignerRequest;
import software.amazon.awssdk.services.cloudfront.url.SignedUrl;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

public class AwsCloudFrontLearningTest {
    private static final String SECRET_NAME = "SecretManager에서 원하는 SecretName 선택";
    private static final Region REGION = Region.of("ap-northeast-2");
//    @Test
    @DisplayName("서명된 URL을 획득하여 콘텐츠를 조회한다.")
    void getContentBySignedUrl() throws Exception {
        // given


        String privateKeyContent = getPrivateKeyFromSecret();
        System.out.println("privateKeyContent = " + privateKeyContent);
        PrivateKey privateKey=null;
        try {
            privateKey = loadPrivateKey(privateKeyContent);
            System.out.println(privateKey);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String distributionDomainName = "d3bzi2jjrffb13.cloudfront.net";
        String fileNameToUpload = "coclimblearningtestfile";
        String publicKeyId="유효한 퍼블릭 키 ID를 입력해주세요";

        // when
        CloudFrontUtilities utilities = CloudFrontUtilities.create();

//        CannedSignerRequest requestForCannedPolicy = createRequestForCannedPolicy(distributionDomainName, fileNameToUpload, privateKey, publicKeyId);
        CustomSignerRequest requestForCustomPolicy = createRequestForCustomPolicy(distributionDomainName, fileNameToUpload, privateKey, publicKeyId);

        SignedUrl signedUrl = utilities.getSignedUrlWithCustomPolicy(requestForCustomPolicy);
        System.out.println("signedUrl = " + signedUrl);
        // then

        String content = fetchContentFromSignedUrl(signedUrl.url());
        System.out.println("content = " + content);
        Assertions.assertThat(content).isNotEmpty();

    }


    public static String fetchContentFromSignedUrl(String signedUrl) {
        StringBuilder content = new StringBuilder();

        try {
            URL url = new URL(signedUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line);
                    content.append("\n");
                }
                reader.close();

                return content.toString();
            } else {
                System.err.println("Failed to fetch content. HTTP response code: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    public static CannedSignerRequest createRequestForCannedPolicy(String distributionDomainName, String fileNameToUpload,
                                                                   PrivateKey privateKey, String publicKeyId) throws Exception{
        String protocol = "https";
        String resourcePath = "/" + fileNameToUpload;

        String cloudFrontUrl = new URL(protocol, distributionDomainName, resourcePath).toString();
        Instant expirationDate = Instant.now().plus(1, ChronoUnit.HOURS);

        return CannedSignerRequest.builder()
                .resourceUrl(cloudFrontUrl)
                .privateKey(privateKey)
                .keyPairId(publicKeyId)
                .expirationDate(expirationDate)
                .build();
    }

    public static CustomSignerRequest createRequestForCustomPolicy(String distributionDomainName, String fileNameToUpload,
                                                                   PrivateKey privateKey, String publicKeyId) throws Exception {
        String protocol = "https";
        String resourcePath = "/" + fileNameToUpload;

        String cloudFrontUrl = new URL(protocol, distributionDomainName, resourcePath).toString();
        Instant expireDate = Instant.now().plus(7, ChronoUnit.DAYS);

        return CustomSignerRequest.builder()
                .resourceUrl(cloudFrontUrl)
                .privateKey(privateKey)
                .keyPairId(publicKeyId)
                .expirationDate(expireDate)
                .build();
    }


    public static PrivateKey loadPrivateKey(String keyContent) throws Exception {
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
        KeyFactory keyFactory = KeyFactory.getInstance("RSA"); // Assuming RSA. Change if needed.
        return keyFactory.generatePrivate(keySpec);
    }

    private String getPrivateKeyFromSecret() {
        // [Simplified method by removing all the prints and some unnecessary variables.]
        SecretsManagerClient client = SecretsManagerClient.builder().region(REGION).build();
        GetSecretValueResponse response = client.getSecretValue(
                GetSecretValueRequest.builder().secretId(SECRET_NAME).build());

        return extractPrivateKeyFromSecret(response.secretString());
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

