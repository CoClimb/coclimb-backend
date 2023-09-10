package swm.s3.coclimb.api.adapter.out.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import org.springframework.stereotype.Component;
import swm.s3.coclimb.api.adapter.out.elasticsearch.dto.GymElasticDto;

import java.io.IOException;
import java.util.List;

@Component
public class GymElasticsearchQuery{

    private final ElasticsearchClient esClient;
    private final ElasticsearchClientManager esClientManager;

    public GymElasticsearchQuery(ElasticsearchClientManager esClientManager) {
        this.esClientManager = esClientManager;
        esClient = esClientManager.getEsClient();
    }

    public List<String> autoCorrectName(String keyword,int size) {
        String index = "gyms";
        String field = "name";
        List<Hit<GymElasticDto>> hits = null;
        try {
            SearchResponse<GymElasticDto> response = esClient.search(
                    esClientManager.createSearchQuery(index, field, keyword, size),
                    GymElasticDto.class);
            hits = response.hits().hits();
        } catch (IOException e) {
            e.printStackTrace();
            //todo error처리
        }
        return hits.stream()
                .map(h -> h.source().getName())
                .toList();

    }



}
