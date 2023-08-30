package swm.s3.coclimb.learning;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import swm.s3.coclimb.api.IntegrationTestSupport;
import swm.s3.coclimb.api.adapter.out.elasticsearch.GymElasticDto;
import swm.s3.coclimb.domain.gym.Gym;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;

public class ElasticsearchLearningTest extends IntegrationTestSupport {

    @AfterEach
    void tearDown() throws Exception {
        ElasticsearchClient esClient = elasticsearchClientManager.getEsClient();
        esClient.indices().delete(d -> d.index("gyms"));
    }

    @Test
    @DisplayName("새로운 인덱스를 생성")
    void esCreateIndex() throws Exception {
        // given
        ElasticsearchClient esClient = elasticsearchClientManager.getEsClient();
        System.out.println(esClient.info());
        // when
        CreateIndexResponse response = esClient.indices().create(c -> c
                .index("gyms")
        );

        // then
        assertThat(response.index()).isEqualTo("gyms");
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
        esClient.indices().create(c -> c.index(searchIndex));

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



//    @Test
//    @DisplayName("엘라스틱서치에서 검색어를 통해 데이터를 가져온다.")
//    void esSearch() throws Exception {
//        // given
//        String searchIndex = "gyms";
//        String searchField = "name";
//        String searchText = "더클라임";
//        // when
//        ElasticsearchClient esClient = elasticsearchClientManager.getEsClient();
//        SearchResponse<?> response = esClient.search(s -> s.index(searchIndex)
//                .source(c -> c
//                        .filter(f -> f
//                                .excludes("id")
//                                .excludes("@timestamp")
//                                .excludes("created_at")
//                                .excludes("last_modified_at")
//                        )
//                )
//                .query(q -> q
//                        .match(t -> t
//                                .field(searchField)
//                                .query(searchText)
//                        )
//                ), GymElasticDto.class);
//        GymElasticDto gymElasticDto = (GymElasticDto) response.hits().hits().get(0).source();
//        Gym sut = gymElasticDto.toDomain();
//
//        // then
//        System.out.println(response);
//        System.out.println(response.hits().hits().size());
//        assertThat(sut.getName()).isNotEmpty();
//    }

}
