package swm.s3.coclimb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import swm.s3.coclimb.api.adapter.out.elasticsearch.ElasticProperties;
import swm.s3.coclimb.config.AWSConfig;
import swm.s3.coclimb.config.security.JwtProperties;

@EnableConfigurationProperties({JwtProperties.class, ElasticProperties.class, AWSConfig.class})
@SpringBootApplication
public class CoclimbApplication {

	static {
		System.setProperty("com.amazonaws.sdk.disableEc2Metadata", "true");
	}
	public static void main(String[] args) {
		SpringApplication.run(CoclimbApplication.class, args);
	}

}
