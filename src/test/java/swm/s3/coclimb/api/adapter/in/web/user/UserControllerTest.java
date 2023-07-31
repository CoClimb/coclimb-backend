package swm.s3.coclimb.api.adapter.in.web.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import swm.s3.coclimb.api.ControllerTestSupport;
import swm.s3.coclimb.domain.user.User;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest extends ControllerTestSupport {

    @Test
    @DisplayName("토큰 정보 기반으로 유저를 조회한다.")
    void getUserByToken() throws Exception {
        // given
        String accessToken = "token";
        given(jwtManager.getSubject(accessToken)).willReturn("1");
        given(userLoadPort.getById(1L)).willReturn(User.builder()
                .name("username")
                .build());
        // when, then
        mockMvc.perform(get("/users/me")
                        .header("Authorization",accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("username"));
    }

}