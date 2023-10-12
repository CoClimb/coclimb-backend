package swm.s3.coclimb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import swm.s3.coclimb.config.propeties.AwsCredentialsProperties;
import swm.s3.coclimb.config.propeties.AwsRdsProperties;
import swm.s3.coclimb.config.propeties.ElasticProperties;
import swm.s3.coclimb.config.propeties.JwtProperties;

@EnableConfigurationProperties({
		JwtProperties.class,
		ElasticProperties.class,
		AwsRdsProperties.class,
		AwsCredentialsProperties.class
})
@SpringBootApplication
public class CoclimbApplication {

	static {
		System.setProperty("com.amazonaws.sdk.disableEc2Metadata", "true");
	}

	public static void main(String[] args) {
		SpringApplication.run(CoclimbApplication.class, args);
	}

}
