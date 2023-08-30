package swm.s3.coclimb.config;

import lombok.Getter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import swm.s3.coclimb.config.aspect.logtrace.LogTraceAspect;
import swm.s3.coclimb.config.aspect.logtrace.LogTraceImpl;
import swm.s3.coclimb.config.security.JwtProperties;

@Getter
@Configuration
public class AppConfig {

    private final JwtProperties jwtProperties;

    public AppConfig(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @Bean
    public LogTraceAspect logTraceAspect() {
        return new LogTraceAspect(new LogTraceImpl());
    }

}
