package swm.s3.coclimb.config;

import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import swm.s3.coclimb.config.aspect.logtrace.LogTraceAspect;
import swm.s3.coclimb.config.aspect.logtrace.LogTraceImpl;

import javax.crypto.SecretKey;
import java.util.Base64;

@Getter
@ConfigurationProperties(prefix = "app-config")
public class AppConfig {
    private final byte[] jwtKey;
    private final long validTime;
    private final SecretKey secretKey;

    public AppConfig(String jwtKey, String validTime) {
        this.jwtKey = Base64.getDecoder().decode(jwtKey);
        this.secretKey = Keys.hmacShaKeyFor(this.jwtKey);
        this.validTime = Long.parseLong(validTime)*1000;
    }

    @Bean
    public LogTraceAspect logTraceAspect() {
        return new LogTraceAspect(new LogTraceImpl());
    }
}
