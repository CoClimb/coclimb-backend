package swm.s3.coclimb.api.adapter.out.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import org.springframework.stereotype.Component;
import swm.s3.coclimb.api.adapter.out.elasticsearch.dto.GymElasticDto;
import swm.s3.coclimb.api.exception.errortype.elasticsearch.InvalidElasticQuery;
import swm.s3.coclimb.domain.gym.Gym;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class GymElasticsearchQuery{

    private final ElasticsearchClient esClient;
    private final ElasticsearchClientManager esClientManager;

    public GymElasticsearchQuery(ElasticsearchClientManager esClientManager) {
        this.esClientManager = esClientManager;
        esClient = esClientManager.getEsClient();
    }

    public List<String> autoCorrectName(String keyword,int size) {
        List<Hit<GymElasticDto>> hits = null;
        try {
            SearchResponse<GymElasticDto> response = esClient.search(
                    esClientManager.createSearchQuery("gyms", "name", keyword, size),
                    GymElasticDto.class);
            hits = response.hits().hits();
        } catch (IOException e) {
            throw new InvalidElasticQuery();
        }
        return hits.stream()
                .map(h -> h.source().getName())
                .toList();

    }

    public Optional<Gym> findByName(String name) {
        List<Hit<GymElasticDto>> hits = null;
        try {
            SearchResponse<GymElasticDto> response = esClient.search(esClientManager.createExactSearchQuery("gyms", "name", name, 1)
                    , GymElasticDto.class);
            hits = response.hits().hits();
        } catch (IOException e) {
            throw new InvalidElasticQuery();
        }
        try {
            GymElasticDto gymElasticDto = hits.get(0).source();
            return Optional.of(gymElasticDto.toDomain());
        } catch (IndexOutOfBoundsException e) {
            return Optional.empty();
        }

    }

}
