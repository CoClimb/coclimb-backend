package swm.s3.coclimb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import swm.s3.coclimb.api.adapter.out.elasticsearch.ElasticProperties;
import swm.s3.coclimb.config.security.JwtProperties;

@EnableConfigurationProperties({JwtProperties.class, ElasticProperties.class})
@SpringBootApplication
public class CoclimbApplication {

	public static void main(String[] args) {
		SpringApplication.run(CoclimbApplication.class, args);
	}

}
