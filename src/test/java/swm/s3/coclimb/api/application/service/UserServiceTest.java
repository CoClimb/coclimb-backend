package swm.s3.coclimb.api.application.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import swm.s3.coclimb.api.adapter.out.user.UserJpaRepository;
import swm.s3.coclimb.api.application.port.out.user.UserLoadPort;
import swm.s3.coclimb.api.application.port.out.user.UserUpdatePort;
import swm.s3.coclimb.api.domain.User;
import swm.s3.coclimb.api.auth.instagram.InstagramRestApiManager;
import swm.s3.coclimb.api.auth.instagram.dto.LongLivedTokenResponseDto;
import swm.s3.coclimb.api.auth.instagram.dto.ShortLivedTokenResponseDto;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@SpringBootTest
@ExtendWith(MockitoExtension.class)
@Transactional
class UserServiceTest {
    UserService userService;
    @Mock
    InstagramRestApiManager instagramRestApiManager;
    @Autowired
    UserLoadPort userLoadPort;
    @Autowired
    UserUpdatePort userUpdatePort;
    @Autowired
    UserJpaRepository userJpaRepository;

    @BeforeEach
    void setUp() {
        userService = new UserService(userLoadPort, userUpdatePort, instagramRestApiManager);
    }

    @AfterEach
    void tearDown() {
        userJpaRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("첫 로그인 시 유저 생성 후 저장한다.")
    void firstLogin() {
        // given
        Long instaUserId = 1L;
        when(instagramRestApiManager.getShortLivedAccessTokenAndUserId(any(String.class))).thenReturn(ShortLivedTokenResponseDto.builder()
                .shortLivedAccessToken("shortLivedAccessToken")
                .userId(instaUserId)
                .build());
        when(instagramRestApiManager.getLongLivedAccessToken(any(String.class))).thenReturn(LongLivedTokenResponseDto.builder()
                .longLivedAccessToken("longLivedAccessToken")
                .expiresIn(5184000L)
                .tokenType("Bearer")
                .build());

        // when
        userService.loginInstagram("test");
        User sut = userLoadPort.findByInstaUserId(instaUserId);

        // then
        verify(instagramRestApiManager, times(1)).getShortLivedAccessTokenAndUserId(any(String.class));
        verify(instagramRestApiManager, times(1)).getLongLivedAccessToken(any(String.class));
        assertThat(sut.getInstaAccessToken()).isEqualTo("longLivedAccessToken");
    }

    @Test
    @DisplayName("이미 유저가 존재하고 토큰 유효 기간이 31일 이상이면 토큰을 갱신하지 않는다.")
    void notUpdateToken() {
        // given
        LocalDate now = LocalDate.now();
        userJpaRepository.save(User.builder()
                .instaUserId(1L)
                .instaAccessToken("longLivedAccessToken")
                .instaTokenExpireDate(now.plusDays(31))
                .build());

        Long instaUserId = 1L;
        when(instagramRestApiManager.getShortLivedAccessTokenAndUserId(any(String.class))).thenReturn(ShortLivedTokenResponseDto.builder()
                .shortLivedAccessToken("shortLivedAccessToken")
                .userId(instaUserId)
                .build());
        lenient().when(instagramRestApiManager.getLongLivedAccessToken(any(String.class))).thenReturn(LongLivedTokenResponseDto.builder()
                .longLivedAccessToken("newLongLivedAccessToken")
                .expiresIn(5184000L)
                .tokenType("Bearer")
                .build());

        // when
        userService.loginInstagram("test");
        User sut = userLoadPort.findByInstaUserId(instaUserId);

        // then
        verify(instagramRestApiManager, times(1)).getShortLivedAccessTokenAndUserId(any(String.class));
        verify(instagramRestApiManager, times(0)).getLongLivedAccessToken(any(String.class));
        verify(instagramRestApiManager, times(0)).refreshLongLivedToken(any(String.class));
        assertThat(sut.getInstaAccessToken()).isEqualTo("longLivedAccessToken");
    }

    @Test
    @DisplayName("이미 유저가 존재하고 토큰 유효 기간이 31일 미만이면 토큰을 갱신한다.")
    void updateToken() {
        // given
        LocalDate now = LocalDate.now();
        Long instaUserId = 1L;

        userJpaRepository.save(User.builder()
                .instaUserId(instaUserId)
                .instaAccessToken("longLivedAccessToken")
                .instaTokenExpireDate(now.plusDays(30))
                .build());

        when(instagramRestApiManager.getShortLivedAccessTokenAndUserId(any(String.class))).thenReturn(ShortLivedTokenResponseDto.builder()
                .shortLivedAccessToken("shortLivedAccessToken")
                .userId(instaUserId)
                .build());
        when(instagramRestApiManager.refreshLongLivedToken(any(String.class))).thenReturn(LongLivedTokenResponseDto.builder()
                .longLivedAccessToken("refreshedAccessToken")
                .expiresIn(5184000L)
                .tokenType("Bearer")
                .build());

        // when
        userService.loginInstagram("test");
        User sut = userLoadPort.findByInstaUserId(instaUserId);

        // then
        verify(instagramRestApiManager, times(1)).getShortLivedAccessTokenAndUserId(any(String.class));
        verify(instagramRestApiManager, times(0)).getLongLivedAccessToken(any(String.class));
        verify(instagramRestApiManager, times(1)).refreshLongLivedToken(any(String.class));
        assertThat(sut.getInstaAccessToken()).isEqualTo("refreshedAccessToken");
    }

    @Test
    @DisplayName("이미 유저가 존재하고 토큰 유효 기간이 끝났으면 토큰을 새로 발급한다.")
    void reissueToken() {
        // given
        LocalDate now = LocalDate.now();
        Long instaUserId = 1L;

        userJpaRepository.save(User.builder()
                .instaUserId(instaUserId)
                .instaAccessToken("expiredAccessToken")
                .instaTokenExpireDate(now.minusDays(1))
                .build());

        when(instagramRestApiManager.getShortLivedAccessTokenAndUserId(any(String.class))).thenReturn(ShortLivedTokenResponseDto.builder()
                .shortLivedAccessToken("shortLivedAccessToken")
                .userId(instaUserId)
                .build());
        when(instagramRestApiManager.getLongLivedAccessToken(any(String.class))).thenReturn(LongLivedTokenResponseDto.builder()
                .longLivedAccessToken("newLongLivedAccessToken")
                .expiresIn(5184000L)
                .tokenType("Bearer")
                .build());

        // when
        userService.loginInstagram("test");
        User sut = userLoadPort.findByInstaUserId(instaUserId);

        // then
        verify(instagramRestApiManager, times(1)).getShortLivedAccessTokenAndUserId(any(String.class));
        verify(instagramRestApiManager, times(1)).getLongLivedAccessToken(any(String.class));
        verify(instagramRestApiManager, times(0)).refreshLongLivedToken(any(String.class));
        assertThat(sut.getInstaAccessToken()).isEqualTo("newLongLivedAccessToken");
    }
}