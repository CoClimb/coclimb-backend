package swm.s3.coclimb.api.adapter.out.instagram;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import swm.s3.coclimb.api.IntegrationTestSupport;
import swm.s3.coclimb.api.adapter.out.instagram.dto.LongLivedTokenResponse;
import swm.s3.coclimb.api.adapter.out.instagram.dto.ShortLivedTokenResponse;
import swm.s3.coclimb.domain.user.InstagramUserInfo;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

class InstagramRestApiManagerTest extends IntegrationTestSupport {

    @Test
    @DisplayName("새로운 인스타그램 토큰을 발급받는다.")
    void getNewInstagram() throws Exception {
        // given
        ShortLivedTokenResponse shortLivedTokenResponse = new ShortLivedTokenResponse("shortToken", 1L);
        LocalDateTime time = LocalDateTime.of(1, 1, 1, 1, 1, 1);

        given(instagramRestApi.getLongLivedToken(any()))
                .willReturn(new LongLivedTokenResponse("longToken","tokenType",1000L));
        given(serverClock.getDateTime()).willReturn(time);

        // when
        InstagramUserInfo sut = instagramRestApiManager.getNewInstagramInfo(shortLivedTokenResponse);

        // then
        assertThat(sut)
                .extracting("id", "accessToken", "tokenExpireTime")
                .containsExactly(1L, "longToken", time.plusSeconds(1000L));
    }

    @Test
    @DisplayName("인스타그램 엑세스 토큰이 만료된 경우, 새로운 토큰을 발급받아 인스타그램 정보를 갱신 한다.")
    void updateInstagramTokenWhenExpired() throws Exception {
        // given
        LocalDateTime time = LocalDateTime.of(1, 1, 1, 1, 1, 1);
        InstagramUserInfo sut = InstagramUserInfo.builder()
                .id(1L)
                .accessToken("oldToken")
                .tokenExpireTime(time)
                .build();
        ShortLivedTokenResponse shortLivedTokenResponse = new ShortLivedTokenResponse("shortToken", 1L);

        given(instagramRestApi.getLongLivedToken(any()))
                .willReturn(new LongLivedTokenResponse("newToken","tokenType",1000L));
        given(serverClock.getDateTime()).willReturn(time);


        // when
        instagramRestApiManager.updateInstagramToken(sut,shortLivedTokenResponse);

        // then
        assertThat(sut)
                .extracting("id", "accessToken", "tokenExpireTime")
                .containsExactly(1L, "newToken", time.plusSeconds(1000L));
    }

    @Test
    @DisplayName("인스타그램 엑세스 토큰 유효기간이 7일 미만인 경우 경우, 유효기간을 연장한다.")
    void updateInstagramTokenWhenNotExpired() throws Exception {
        // given
        LocalDateTime time = LocalDateTime.of(1, 1, 1, 1, 1, 1);
        InstagramUserInfo sut = InstagramUserInfo.builder()
                .id(1L)
                .accessToken("token")
                .tokenExpireTime(time.plusDays(6))
                .build();
        ShortLivedTokenResponse shortLivedTokenResponse = new ShortLivedTokenResponse("shortToken", 1L);

        given(instagramRestApi.refreshLongLivedToken(any()))
                .willReturn(new LongLivedTokenResponse("token","tokenType",1000L));
        given(serverClock.getDateTime()).willReturn(time);


        // when
        instagramRestApiManager.updateInstagramToken(sut,shortLivedTokenResponse);

        // then
        assertThat(sut)
                .extracting("id", "accessToken", "tokenExpireTime")
                .containsExactly(1L, "token", time.plusSeconds(1000L));
    }

    @Test
    @DisplayName("인스타그램 엑세스 토큰 유효기간이 7일 이상인 경우 경우, 인스타그램 토큰을 갱신하지 않는다.")
    void notUpdateInstagramToken() throws Exception {
        // given
        LocalDateTime time = LocalDateTime.of(1, 1, 1, 1, 1, 1);
        InstagramUserInfo sut = InstagramUserInfo.builder()
                .id(1L)
                .accessToken("token")
                .tokenExpireTime(time.plusDays(7))
                .build();
        ShortLivedTokenResponse shortLivedTokenResponse = new ShortLivedTokenResponse("shortToken", 1L);
        given(serverClock.getDateTime()).willReturn(time);


        // when
        instagramRestApiManager.updateInstagramToken(sut,shortLivedTokenResponse);

        // then
        assertThat(sut)
                .extracting("id", "accessToken", "tokenExpireTime")
                .containsExactly(1L, "token", time.plusDays(7));
    }

}