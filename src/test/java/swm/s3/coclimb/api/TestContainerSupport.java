package swm.s3.coclimb.api;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.elasticsearch.ElasticsearchContainer;

public class TestContainerSupport implements AfterAllCallback {

    public static MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:latest")
            .withCommand("--character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")
            .withCommand("--character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci")
            .withExposedPorts(3306);

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        // mysql
        registry.add("spring.datasource.url", () -> mysqlContainer.getJdbcUrl());
        registry.add("spring.datasource.driverClassName", () -> mysqlContainer.getDriverClassName());
        registry.add("spring.datasource.username", () -> mysqlContainer.getUsername());
        registry.add("spring.datasource.password", () -> mysqlContainer.getPassword());
    }
    public static ElasticsearchContainer elasticsearchContainer = new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:8.9.0")
            .withPassword("test")
            .withExposedPorts(9200);

    protected ElasticsearchClient getTestEsClient() {
        BasicCredentialsProvider credentialProvider = new BasicCredentialsProvider();
        credentialProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials("elastic", "test"));

        RestClient restClient = RestClient
                .builder(HttpHost.create("https://localhost:" + elasticsearchContainer.getMappedPort(9200)))
                .setHttpClientConfigCallback(hc -> hc
                        .setSSLContext(elasticsearchContainer.createSslContextFromCa())
                        .setDefaultCredentialsProvider(credentialProvider)
                )
                .build();

        // Create the transport and the API client
        ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        return new ElasticsearchClient(transport);
    }

    static {
        mysqlContainer.start();
        elasticsearchContainer.start();
    }


    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        mysqlContainer.stop();
        elasticsearchContainer.stop();
    }
}
