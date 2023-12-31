package swm.s3.coclimb.api.adapter.in.web.search;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import swm.s3.coclimb.api.ControllerTestSupport;
import swm.s3.coclimb.api.application.port.in.search.dto.SearchNameResult;
import swm.s3.coclimb.api.exception.FieldErrorType;
import swm.s3.coclimb.domain.document.Document;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SearchControllerTest extends ControllerTestSupport {

    @Test
    @DisplayName("키워드와 검색 대상을 입력받아 자동완성된 검색결과를 반환한다.")
    void autoCompleteGymNames() throws Exception {
        // given
        SearchNameResult searchNameResult = SearchNameResult.builder()
                .name("더클라임 클라이밍 짐앤샵 신림점")
                .type(Document.GYM.getIndex())
                .build();
        String keyword = "더클 서울";
        int target = 0;

        given(searchQuery.autoComplete(anyString(), anyList(), anyInt()))
                .willReturn(List.of(searchNameResult));

        // when, then
        mockMvc.perform(get("/search/autocomplete")
                        .queryParam("keyword",keyword)
                        .queryParam("targets", String.valueOf(target))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("더클라임 클라이밍 짐앤샵 신림점"))
                .andExpect(jsonPath("$[0].type").value(target));
    }

    @Test
    @DisplayName("잘못된 타겟에 대해 자동완성 요청 시 예외가 발생한다.")
    void autoCompleteGymNamesFailWithWrongTarget() throws Exception {
        // given
        SearchNameResult searchNameResult = SearchNameResult.builder()
                .name("더클라임 클라이밍 짐앤샵 신림점")
                .type(Document.GYM.getIndex())
                .build();
        String keyword = "더클 서울";
        int target = -1;

        given(searchQuery.autoComplete(anyString(), anyList(), anyInt()))
                .willReturn(List.of(searchNameResult));

        // when, then
        mockMvc.perform(get("/search/autocomplete")
                        .queryParam("keyword",keyword)
                        .queryParam("targets", String.valueOf(target))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.fields").isMap())
                .andExpect(jsonPath("$.fields.targets").value(target+FieldErrorType.INVALID_VALUE));
    }

//    @Test
//    @DisplayName("키워드를 입력하면 자동완성된 암장 리스트를 제공한다.")
//    void autoCompleteGymNames() throws Exception {
//        // given
//        given(gymQuery.autoCompleteGymNames(any(), anyInt())).willReturn(IntStream.range(0, 10)
//                .mapToObj(i -> new String("암장" + i))
//                .toList());
//
//        // when, then
//        ResultActions result = mockMvc.perform(get("/gyms/autocomplete")
//                        .param("keyword", "더클라임"))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.gymNames").isArray())
//                .andExpect(jsonPath("$.count").value(10))
//                .andExpect(jsonPath("$.gymNames[0]").value("암장0"))
//                .andExpect(jsonPath("$.gymNames[1]").value("암장1"));
//    }
//
//    @Test
//    @DisplayName("자동완성 시 키워드가 없으면 예외가 발생한다.")
//    void autoCompleteGymNamesWithoutKeyword() throws Exception {
//        // given
//        given(gymQuery.autoCompleteGymNames(any(), anyInt())).willReturn(IntStream.range(0, 10)
//                .mapToObj(i -> new String("암장" + i))
//                .toList());
//
//        // when, then
//        ResultActions result = mockMvc.perform(get("/gyms/autocomplete"))
//                .andDo(print())
//                .andExpect(status().isBadRequest());
//    }
}
