package swm.s3.coclimb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import swm.s3.coclimb.config.AppConfig;

@EnableConfigurationProperties(AppConfig.class)
@SpringBootApplication
public class CoclimbApplication {

	public static void main(String[] args) {
		SpringApplication.run(CoclimbApplication.class, args);
	}

}
