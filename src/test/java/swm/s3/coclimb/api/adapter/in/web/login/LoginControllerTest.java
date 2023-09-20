package swm.s3.coclimb.api.adapter.in.web.login;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import swm.s3.coclimb.api.ControllerTestSupport;
import swm.s3.coclimb.api.adapter.in.web.login.dto.InstagramLoginRequest;
import swm.s3.coclimb.api.exception.FieldErrorType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class LoginControllerTest extends ControllerTestSupport {

    @Test
    @DisplayName("인스타그램 로그인 페이지를 반환한다.")
    void redirectInstagramLoginPage() throws Exception {
        mockMvc.perform(get("/login/instagram"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.loginPageUrl").isString())
                .andExpect(jsonPath("$.loginPageUrl")
                        .value(Matchers.containsString("https://api.instagram.com/oauth/authorize")));
    }

    @Test
    @DisplayName("인스타그램으로 로그인에 성공하면 엑세스 토큰을 발급한다.")
    void loginWithInstagram() throws Exception {
        // given
        InstagramLoginRequest request = InstagramLoginRequest.of("123");
        given(loginCommand.loginWithInstagram(any())).willReturn(1L);
        given(jwtManager.issueToken(any())).willReturn("token");
        // when, then
        mockMvc.perform(post("/login/instagram")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isString());
    }
    @Test
    @DisplayName("인스타그램으로 로그인 시 code는 필수값이다.")
    void loginWithInstagramWithNoCode() throws Exception {
        // given
        InstagramLoginRequest request = InstagramLoginRequest.of(null);

        // when, then
        mockMvc.perform(post("/login/instagram")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.fields").exists())
                .andExpect(jsonPath("$.fields.code").value(FieldErrorType.NOT_NULL));
    }
}