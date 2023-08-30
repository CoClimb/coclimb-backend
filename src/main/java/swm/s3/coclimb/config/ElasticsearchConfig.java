package swm.s3.coclimb.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.TransportUtils;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import lombok.Getter;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import swm.s3.coclimb.api.adapter.out.elasticsearch.ElasticProperties;

@Getter
@Profile({"local", "dev"})
@Configuration
public class ElasticsearchConfig {

    private final ElasticProperties elasticProperties;

    public ElasticsearchConfig(ElasticProperties elasticProperties) {
        this.elasticProperties = elasticProperties;
    }

    @Bean
    public ElasticsearchClient elasticsearchClient() {

        // Create the low-level client
        RestClient restClient = RestClient
                .builder(HttpHost.create(elasticProperties.getServerUrl()))
                .setDefaultHeaders(new Header[]{
                        new BasicHeader("Authorization", "ApiKey " + elasticProperties.getApiKey())
                })
                .setHttpClientConfigCallback(hc -> hc
                        .setSSLContext(TransportUtils
                                .sslContextFromCaFingerprint(elasticProperties.getFingerPrint()))
                )
                .build();

        // Create the transport with a Jackson mapper
        ElasticsearchTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper());

        // And create the API client
        return new ElasticsearchClient(transport);
    }

}
