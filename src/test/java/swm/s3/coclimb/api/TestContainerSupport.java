package swm.s3.coclimb.api;

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
