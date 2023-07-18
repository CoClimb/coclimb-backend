package swm.s3.coclimb.api.auth.instagram;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class InstagramWebClient{

    @Bean
    public WebClient basicDisplayClient() {
        return WebClient.create("https://api.instagram.com");
    }

    @Bean
    public WebClient graphClient() {
        return WebClient.create("https://graph.instagram.com");
    }
}
