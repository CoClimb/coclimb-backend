package swm.s3.coclimb.api.adapter.out.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import org.springframework.stereotype.Component;

@Component
public class ElasticsearchClientManager {

    private final ElasticsearchClient esClient;

    public ElasticsearchClientManager(ElasticsearchClient esClient) {
        this.esClient = esClient;
    }

    public ElasticsearchClient getEsClient() {
        return esClient;
    }



}
