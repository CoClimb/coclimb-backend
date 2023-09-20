package swm.s3.coclimb.api.docs;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;
import swm.s3.coclimb.api.RestDocsTestSupport;
import swm.s3.coclimb.api.adapter.in.web.report.dto.ReportCreateRequest;
import swm.s3.coclimb.domain.report.Report;
import swm.s3.coclimb.domain.user.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ReportControllerDocsTest extends RestDocsTestSupport {

    @Test
    @DisplayName("사용자 제보를 생성하는 API")
    void createReport() throws Exception {
        // given
        userJpaRepository.save(User.builder().build());
        User user = userJpaRepository.findAll().get(0);
        ReportCreateRequest request = ReportCreateRequest.builder()
                .subject("subject")
                .description("description")
                .build();
        //when
        ResultActions result = mockMvc.perform(post("/reports")
                .header("Authorization", jwtManager.issueToken(String.valueOf(user.getId())))
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
        Report report = reportJpaRepository.findAll().get(0);
        // then
        assertThat(report.getUser().getId()).isEqualTo(user.getId());
        assertThat(report.getSubject()).isEqualTo(request.getSubject());
        assertThat(report.getDescription()).isEqualTo(request.getDescription());
        result.andExpect(status().isCreated());

        // docs
        result.andDo(document("report-create",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                        headerWithName("Authorization").description("JWT 인증 토큰")
                ),
                requestFields(
                        fieldWithPath("subject").description("제보 주제"),
                        fieldWithPath("description").description("제보 내용")
                )
        ));
    }
}
