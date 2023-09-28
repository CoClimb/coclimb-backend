package swm.s3.coclimb.api.adapter.out.elasticsearch;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "app-config.elastic")
public class ElasticProperties {

    private final String serverUrl;
    private final String apiKey;
    private final String fingerPrint;
    private final String userName;
    private final String password;

    public ElasticProperties(String serverUrl, String apiKey, String fingerPrint, String userName, String password) {
        this.serverUrl = serverUrl;
        this.apiKey = apiKey;
        this.fingerPrint = fingerPrint;
        this.userName = userName;
        this.password = password;
    }
}
