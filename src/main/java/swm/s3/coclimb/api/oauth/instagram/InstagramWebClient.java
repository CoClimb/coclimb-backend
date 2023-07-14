package swm.s3.coclimb.api.oauth.instagram;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class InstagramWebClient{

    @Bean
    public WebClient webClient() {
        return WebClient.create("https://api.instagram.com");
    }
}
