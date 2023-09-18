package swm.s3.coclimb.learning;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import swm.s3.coclimb.api.IntegrationTestSupport;
import swm.s3.coclimb.api.adapter.out.elasticsearch.dto.GymElasticDto;
import swm.s3.coclimb.domain.gym.Gym;

import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;

public class ElasticsearchLearningTest extends IntegrationTestSupport {

    @Test
    @DisplayName("새로운 인덱스를 생성")
    void esCreateIndex() throws Exception {
        // given
        ElasticsearchClient esClient = elasticsearchClientManager.getEsClient();
        System.out.println(esClient.info());
        // when
        CreateIndexResponse response = esClient.indices().create(c -> c
                .index("new-index")
        );

        // then
        assertThat(response.index()).isEqualTo("new-index");
    }

    @Test
    @DisplayName("새 document 삽입")
    void indexingDocument() throws Exception {
        // given
        String index = "gyms";
        ElasticsearchClient esClient = elasticsearchClientManager.getEsClient();
        Gym gym = Gym.builder()
                .name("클라이밍")
                .build();

        // when
        IndexResponse response = esClient.index(i -> i
                .index(index)
                .id("1")
                .document(GymElasticDto.fromDomain(gym)));
        GetResponse<GymElasticDto> sut = esClient.get(rq -> rq.index(index).id("1"), GymElasticDto.class);

        // then
        assertThat(sut.source().getName()).isEqualTo("클라이밍");
        assertThat(response.result().name()).isEqualTo("Created");
    }

    @Test
    @DisplayName("벌크연산")
    void bulkDocuments() throws Exception {
        // given
        String index = "gyms";
        ElasticsearchClient esClient = elasticsearchClientManager.getEsClient();
        List<Gym> gyms = LongStream.range(0, 5)
                .mapToObj(i -> Gym.builder()
//                        .id(i)
                        .name("클라이밍" + i)
                        .build())
                .toList();

        BulkRequest.Builder br = new BulkRequest.Builder();
        for (Gym gym : gyms) {
            br.operations(op -> op
                    .index(idx -> idx
                                    .index(index)
//                            .id(gym.getId().toString())
                                    .document(GymElasticDto.fromDomain(gym)
                                    )
                    ));
        }


        // when
        BulkResponse result = esClient.bulk(br.build());
        esClient.indices().refresh();
//        GetResponse<GymElasticDto> sut = esClient.get(rq -> rq.index(index).id(gyms.get(0).getId().toString()), GymElasticDto.class);
        CountResponse count = esClient.count(r -> r
                .index(index));
        // then

        assertThat(count.count()).isEqualTo(gyms.size());
//        assertThat(sut.source().getName()).isEqualTo("클라이밍0");
        assertThat(result.items().size()).isEqualTo(gyms.size());
    }

    @Test
    @DisplayName("검색")
    void searchDocuments() throws Exception {
        // given
        String searchIndex = "gyms";
        String searchField = "name";
        String searchText = "클라이밍";

        ElasticsearchClient esClient = elasticsearchClientManager.getEsClient();
//        esClient.indices().create(c -> c.index(searchIndex));

        List<Gym> gyms = IntStream.range(0, 3)
                .mapToObj(i -> Gym.builder()
                        .name("클라이밍 " + i)
                        .build())
                .toList();
        BulkRequest.Builder br = new BulkRequest.Builder();
        for (Gym gym : gyms) {
            br.operations(op -> op
                    .index(idx -> idx
                            .index(searchIndex)
                            .document(GymElasticDto.fromDomain(gym))
                    )
            );
        }
        BulkResponse result = esClient.bulk(br.build());
        esClient.indices().refresh();
        // when
        SearchResponse<GymElasticDto> sut = esClient.search(s -> s.index(searchIndex)
                .source(c -> c
                        .filter(f -> f
                                .excludes("id")
                                .excludes("@timestamp")
                                .excludes("created_at")
                                .excludes("last_modified_at")
                        )
                )
                .query(q -> q
                        .match(t -> t
                                .field(searchField)
                                .query(searchText)
                        )
                ), GymElasticDto.class);

        // then
        List<Hit<GymElasticDto>> hits = sut.hits().hits();
        assertThat(hits.size()).isEqualTo(gyms.size());

        assertThat(hits).extracting(h -> h.source().getName())
                .containsExactlyInAnyOrder("클라이밍 0", "클라이밍 1", "클라이밍 2");
    }

    @Test
    @DisplayName("한글 분석기 적용")
    void searchAnalyzer() throws Exception {
        // given
        String searchIndex = "gyms";
        ElasticsearchClient esClient = elasticsearchClientManager.getEsClient();

        Reader input = new StringReader(Files.readString(Path.of("src/test/resources/docker/elastic/gyms.json")));
//        esClient.indices().create(c -> c
//                .index("gyms")
//                .withJson(input));

        IndexResponse response = esClient.index(i -> i
                .index(searchIndex)
                .id("1")
                .document(GymElasticDto.fromDomain(Gym.builder()
                        .name("클라이밍")
                        .build())));
        esClient.indices().refresh();
        // when
        SearchResponse<GymElasticDto> sut = esClient.search(s -> s.index(searchIndex)
                .source(c -> c
                        .filter(f -> f
                                .excludes("id")
                                .excludes("@timestamp")
                                .excludes("created_at")
                                .excludes("last_modified_at")
                        )
                )
                .query(q -> q
                        .multiMatch(t -> t
                                .query("클라")
                                .fields("name","name.nori", "name.ngram")
                        )
                ), GymElasticDto.class);

        // then
        Gym out = sut.hits().hits().get(0).source().toDomain();
        assertThat(out.getName()).isEqualTo("클라이밍");
    }

}
