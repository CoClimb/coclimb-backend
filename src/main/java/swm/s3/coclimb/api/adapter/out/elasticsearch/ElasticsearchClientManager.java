package swm.s3.coclimb.api.adapter.out.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.util.ObjectBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class ElasticsearchClientManager {

    private final ElasticsearchClient esClient;

    public ElasticsearchClient getEsClient() {
        return esClient;
    }

    protected Function<SearchRequest.Builder, ObjectBuilder<SearchRequest>> createSearchQuery(String index, String field, String keyword, int size) {
        return s -> s.index(index)
                .source(c -> c
                        .filter(f -> f
                                .includes(field)
                        )
                )
                .size(size)
                .query(q -> q
                        .multiMatch(t -> t
                                .query(keyword)
                                .fields(field+".nori", field+".ngram")
                        )
                );
    }

    protected Function<SearchRequest.Builder, ObjectBuilder<SearchRequest>> createExactSearchQuery(String index, String field, String keyword, int size) {
        return s -> s.index(index)
                .size(size)
                .query(q -> q
                        .bool(b -> b
                                .must(m -> m
                                        .matchPhrase(mq->mq.field(field).query(keyword))
//                                        .term(t -> t.field(field).value(keyword))
                                )
                        )
                );
    }

}
