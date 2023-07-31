package swm.s3.coclimb.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import swm.s3.coclimb.api.application.port.out.persistence.user.UserLoadPort;
import swm.s3.coclimb.config.argumentresolver.LoginUserArgumentResolver;
import swm.s3.coclimb.config.interceptor.AuthInterceptor;
import swm.s3.coclimb.config.security.JwtManager;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    @Value("${front_end.site_url}")
    private String frontEndSiteUrl;
    private final JwtManager jwtManager;
    private final UserLoadPort userLoadPort;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthInterceptor(jwtManager))
                .order(1);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(frontEndSiteUrl)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true)
                .maxAge(1800);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
            resolvers.add(new LoginUserArgumentResolver(jwtManager,userLoadPort));
    }
}
