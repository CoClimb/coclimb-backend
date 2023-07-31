package swm.s3.coclimb.api.docs;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;
import swm.s3.coclimb.api.RestDocsTestSupport;
import swm.s3.coclimb.domain.user.User;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerDocsTest extends RestDocsTestSupport {

    @Test
    @DisplayName("엑세스 토큰으로 현재 로그인 유저의 정보를 조회하는 API")
    void getUserByToken() throws Exception {
        // given
        Long userId = userJpaRepository.save(User.builder()
                .name("유저").build()).getId();
        String accessToken = jwtManager.issueToken(userId.toString());

        // when, then
        ResultActions result = mockMvc.perform(get("/users/me")
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("유저"));

        // docs
        result.andDo(document("user-myinfo",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                responseFields(
                        fieldWithPath("username").type(JsonFieldType.STRING)
                                .description("사용자이름")
                )));

    }

}