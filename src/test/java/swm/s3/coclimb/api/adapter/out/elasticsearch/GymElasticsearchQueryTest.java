package swm.s3.coclimb.api.adapter.out.elasticsearch;

import co.elastic.clients.elasticsearch.core.BulkRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import swm.s3.coclimb.api.IntegrationTestSupport;
import swm.s3.coclimb.api.adapter.out.elasticsearch.dto.GymElasticDto;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class GymElasticsearchQueryTest extends IntegrationTestSupport {

    private static Stream<Arguments> autoCorrectName() {
        return Stream.of(
                Arguments.of("더클 서울대", "더클라임 클라이밍 짐앤샵 서울대점"),
                Arguments.of("락트리 분당", "락트리클라이밍 분당"),
                Arguments.of("서울숲 잠실", "서울숲클라이밍 잠실점"),
                Arguments.of("더클B", "더클라임 B 홍대점"),
                Arguments.of("신림 더클라임", "더클라임 클라이밍 짐앤샵 신림점"));
    }
    @ParameterizedTest
    @MethodSource
    @DisplayName("이름 입력시 유사도 순으로 암장명을 size만큼 조회한다.")
    void autoCorrectName(String keyword, String expected) throws Exception{
        // given
        List<String> gymNames = readFileToList("src/test/resources/docker/elastic/gyms.txt");

        BulkRequest.Builder br = new BulkRequest.Builder();
        for (String gymName : gymNames) {
            br.operations(op -> op
                    .index(idx -> idx
                            .index("gyms")
                            .document(GymElasticDto.builder()
                                    .name(gymName)
                                    .build())
                    )
            );
        }
        esClient.bulk(br.build());
        esClient.indices().refresh();

        // when
        List<String> sut = gymElasticsearchQuery.autoCorrectName(keyword, 10);

        System.out.println(sut);
        // then
        assertThat(sut.get(0)).isEqualTo(expected);
    }
    private List<String> readFileToList(String filePath) {
        List<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return lines;
    }
}