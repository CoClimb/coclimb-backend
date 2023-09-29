package swm.s3.coclimb.docker;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectContainerResponse.ContainerState;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.ssl.SSLContextBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.testcontainers.DockerClientFactory;

import static swm.s3.coclimb.docker.DockerContainerName.ELASTICSEARCH;
import static swm.s3.coclimb.docker.DockerContainerName.MYSQL;

@Slf4j
@Getter
public class DockerComposeRunner {

    private DockerClient dockerClient = DockerClientFactory.lazyClient();
    private CommandProcessor commandProcessor = new CommandProcessor();
    private ElasticsearchClient elasticsearchClient = elasticsearchClient();

    public void runTestContainers() {
        if (!isHealthy(ELASTICSEARCH) || !isHealthy(MYSQL)) {
            log.info("새 테스트 컨테이너 실행");
            composeUp();
            waitForHealthy(MYSQL);
            waitForHealthy(ELASTICSEARCH);
            checkElasticsearchConnect();
        }

    }

    public void checkElasticsearchConnect() {

        while (true) {
            try {
                log.info("Connecting Elasticsearch...");
                elasticsearchClient.info();
                log.info("Connected Successful");
                break;

            } catch (Exception e) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                log.error("Connecting Fail");
            }
        }
    }


    private void composeUp() {
        log.info("DockerComposeRunner.composeUp");
        commandProcessor.process("docker-compose up -d",
                "src/test/resources/docker");
    }

    public static void composeDown() {
        log.info("DockerComposeRunner.composeDown");
        new CommandProcessor().process("docker-compose down",
                "src/test/resources/docker");
    }

    public ContainerState getContainerState(String containerName) {
        return dockerClient.inspectContainerCmd(containerName).exec().getState();
    }

    public boolean isHealthy(DockerContainerName container) {
        try {
            log.info("Checking health for {}", container.getValue());
            return getContainerState(container.getValue()).getRunning();
        } catch (Exception e) {
            return false;
        }

    }

    public void waitForHealthy(DockerContainerName container) {
        while (!isHealthy(container)) {
        }
    }

    public ElasticsearchClient elasticsearchClient() {
        // Create the low-level client
        RestClientBuilder restClientBuilder = RestClient
                .builder(HttpHost.create("https://localhost:9200"));

        BasicCredentialsProvider credentialProvider = new BasicCredentialsProvider();
        credentialProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials("elastic", "test"));
        RestClient restClient = restClientBuilder.setHttpClientConfigCallback(hc -> {
            try {
                return hc
                        .setSSLContext(new SSLContextBuilder().loadTrustMaterial(null, TrustAllStrategy.INSTANCE).build())
                        .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                        .setDefaultCredentialsProvider(credentialProvider);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).build();

        // Create the transport with a Jackson mapper
        ElasticsearchTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper());

        // And create the API client
        return new ElasticsearchClient(transport);
    }

}
