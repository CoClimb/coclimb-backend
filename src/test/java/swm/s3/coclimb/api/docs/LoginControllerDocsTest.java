package swm.s3.coclimb.api.docs;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;
import swm.s3.coclimb.api.RestDocsTestSupport;
import swm.s3.coclimb.api.adapter.in.web.login.dto.InstagramLoginRequest;
import swm.s3.coclimb.api.adapter.out.instagram.dto.ShortLivedTokenResponse;
import swm.s3.coclimb.domain.user.InstagramUserInfo;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LoginControllerDocsTest extends RestDocsTestSupport {

    @Test
    @DisplayName("/login/instagram 으로 접속하면 인스타그램 로그인 페이지로 리다이렉트 하는 API")
    void redirectInstagramLoginPage() throws Exception {

        //given, when, then
        ResultActions results = mockMvc.perform(get("/login/instagram"))
                .andExpect(status().is3xxRedirection())
                .andExpect(
                        result -> result.getResponse().getRedirectedUrl().contains("https://api.instagram.com/oauth/authorize?client_id="))
                .andExpect(
                        result -> result.getResponse().getRedirectedUrl().contains("&redirect_uri="))
                .andExpect(
                        result -> result.getResponse().getRedirectedUrl().contains("&scope=user_profile,user_media&response_type=code"));
        // docs
        results.andDo(document("login-redirect",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())));
    }

    @Test
    @DisplayName("인스타그램으로 로그인에 성공하면 엑세스 토큰을 발급하는 API")
    void loginWithInstagram() throws Exception {

        // given
        InstagramLoginRequest request = InstagramLoginRequest.of("sample-code");
        String instagramToken = "insta-token";
        Long instagramUserId = 1L;
        given(instagramRestApiManager.getShortLivedTokenAndUserId(any()))
                .willReturn(new ShortLivedTokenResponse(instagramToken, instagramUserId));
        given(instagramRestApiManager.getNewInstagramInfo(any()))
                .willReturn(InstagramUserInfo.builder()
                        .id(instagramUserId)
                        .accessToken(instagramToken)
                        .build());
        // when, then
        ResultActions result = mockMvc.perform(post("/login/instagram")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isString());

        // docs
        result.andDo(document("login-instagram",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                        fieldWithPath("code").type(JsonFieldType.STRING)
                                .description("인스타그램 로그인 성공 후에 받은 코드")
                ),
                responseFields(
                        fieldWithPath("accessToken").type(JsonFieldType.STRING)
                                .description("coclimb access token")
                )));
    }
}