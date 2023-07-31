package swm.s3.coclimb.security;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import swm.s3.coclimb.api.IntegrationTestSupport;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.mockito.BDDMockito.given;

class JwtManagerTest extends IntegrationTestSupport {

    @Test
    @DisplayName("새로운 JWT 토큰을 발급한다.")
    void issueToken() throws Exception {
        // given
        String subject = "userId";
        LocalDateTime iat = LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0, 0));
        given(serverClock.getDateTime())
                .willReturn(iat);
        // when
        String sut = jwtManager.issueToken(subject);
        // then
        Assertions.assertThat(sut).isNotEmpty();
    }
}