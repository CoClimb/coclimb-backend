package swm.s3.coclimb.api.application.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import swm.s3.coclimb.api.IntegrationTestSupport;
import swm.s3.coclimb.api.adapter.out.instagram.InstagramRestApiManager;
import swm.s3.coclimb.api.adapter.out.instagram.dto.ShortLivedTokenResponse;
import swm.s3.coclimb.api.application.port.in.user.UserCommand;
import swm.s3.coclimb.api.application.port.in.user.UserQuery;
import swm.s3.coclimb.domain.user.InstagramUserInfo;
import swm.s3.coclimb.domain.user.User;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;


@Transactional
class LoginServiceTest extends IntegrationTestSupport {

    LoginService loginService;

    @Mock
    InstagramRestApiManager instagramRestApiManager;
    @Autowired UserCommand userCommand;
    @Autowired UserQuery userQuery;
    @BeforeEach
    void setUP(){
        loginService = new LoginService(instagramRestApiManager, userCommand, userQuery);
    }

    @AfterEach
    void tearDown() {
        userJpaRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("인스타그램으로 첫 로그인 시 새로운 유저를 생성한 뒤 인스타그램 정보를 저장한다.")
    void firstLoginByInstagram(){
        // given
        String code = "12345";
        String token = "token";
        Long instagramUserId = 123L;
        given(instagramRestApiManager.getShortLivedTokenAndUserId(any()))
                .willReturn(new ShortLivedTokenResponse(token, instagramUserId));
        given(instagramRestApiManager.getNewInstagramInfo(any()))
                .willReturn(InstagramUserInfo.builder()
                        .id(instagramUserId)
                        .accessToken(token)
                        .build());

        // when
        Long userId = loginService.loginWithInstagram(code);
        Optional<User> sut = userJpaRepository.findById(userId);
        // then
        then(instagramRestApiManager).should().getNewInstagramInfo(any());
        assertThat(sut).isNotNull();
        assertThat(sut.get())
                .extracting("instagramUserInfo.id", "instagramUserInfo.accessToken")
                .containsExactly(instagramUserId, token);
    }

    @Test
    @DisplayName("기존 유저가 인스타그램으로 로그인 시 갱신 로직을 수행한 뒤, 유저 정보를 조회한다.")
    void loginByInstagram() throws Exception {
        // given
        String code = "12345";
        String token = "token";
        Long instagramUserId = 123L;
        userJpaRepository.save(User.builder()
                .name("사용자")
                .instagramUserInfo(InstagramUserInfo.builder()
                        .id(instagramUserId)
                        .accessToken(token)
                        .build())
                .build());

        given(instagramRestApiManager.getShortLivedTokenAndUserId(any()))
                .willReturn(new ShortLivedTokenResponse(token, instagramUserId));

        // when
        Long userId = loginService.loginWithInstagram(code);
        Optional<User> sut = userJpaRepository.findById(userId);
        // then
        then(instagramRestApiManager).should().updateInstagramToken(any(),any());
        assertThat(sut).isNotNull();
        assertThat(sut.get())
                .extracting("instagramUserInfo.id", "instagramUserInfo.accessToken")
                .containsExactly(instagramUserId, token);
    }

}