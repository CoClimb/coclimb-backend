package swm.s3.coclimb.api.adapter.in.user;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import swm.s3.coclimb.api.adapter.out.user.UserJpaRepository;
import swm.s3.coclimb.api.application.port.in.user.UserCommand;
import swm.s3.coclimb.api.application.port.in.user.UserQuery;
import swm.s3.coclimb.api.auth.SessionUser;
import swm.s3.coclimb.api.auth.instagram.InstagramOAuthRecord;
import swm.s3.coclimb.api.domain.User;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class UserControllerTest {
    MockMvc mockMvc;
    @Autowired
    UserController userController;
    @Autowired
    InstagramOAuthRecord instagramOAuthRecord;
    @Autowired
    UserCommand userCommand;
    @Autowired
    UserQuery userQuery;
    @Autowired
    UserJpaRepository userJpaRepository;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @AfterEach
    void tearDown() {
        userJpaRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("/login/instagram 접속 시 인스타그램 로그인 페이지로 리다이렉트 된다.")
    void loginInstagram() throws Exception {
        mockMvc.perform(get("/login/instagram"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "https://api.instagram.com/oauth/authorize?client_id=" + instagramOAuthRecord.clientId()
                        + "&redirect_uri=" + instagramOAuthRecord.redirectUri()
                        + "&scope=user_profile,user_media&response_type=code"));
    }

    @Test
    @DisplayName("/users/me 호출 시 세션 유저의 정보를 조회한다.")
    void getMe() throws Exception {
        //given
        User user = User.builder()
                .instaUserId(1234L)
                .instaAccessToken("testToken")
                .build();

        userJpaRepository.save(user);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", SessionUser.builder()
                .userId(1L)
                .instaUserId(1234L)
                .build());

        //when
        //then
        mockMvc.perform(get("/users/me")
                .session(session))
                .andExpect(jsonPath("instaUserId").value(1234L))
                .andExpect(jsonPath("instaAccessToken").value("testToken"))
                .andExpect(status().isOk());
    }
}