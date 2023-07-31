package swm.s3.coclimb.api.docs;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;
import swm.s3.coclimb.api.RestDocsTestSupport;
import swm.s3.coclimb.api.adapter.in.web.gym.dto.GymCreateRequest;
import swm.s3.coclimb.api.exception.FieldErrorType;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ExceptionControllerDocsTest extends RestDocsTestSupport {

    @Test
    @DisplayName("에러 발생 시 수행되는 실패 응답 API")
    void errorResponse() throws Exception {
        // given
        GymCreateRequest request = GymCreateRequest.builder()
                .build();
        String accessToken = jwtManager.issueToken("docs");

        // when, then
        ResultActions result = mockMvc.perform(post("/gyms")
                        .header("Authorization", accessToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.fields").isMap())
                .andExpect(jsonPath("$.fields.name").value(FieldErrorType.NOT_BLANK));

        // docs
        result.andDo(document("error-response",
                preprocessResponse(prettyPrint()),
                responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING)
                                .description("에러 메세지"),
                        fieldWithPath("fields").type(JsonFieldType.OBJECT)
                                .description("에러가 발생한 필드 목록"),
                        fieldWithPath("fields.name").type(JsonFieldType.STRING)
                                .description("에러 필드 및 에러 메세지 예시")
                )
        ));
    }

}