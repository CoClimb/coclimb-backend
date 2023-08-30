package swm.s3.coclimb.learning;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import swm.s3.coclimb.api.IntegrationTestSupport;
import swm.s3.coclimb.config.ServerClock;

import javax.crypto.SecretKey;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Base64;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

public class JwtLearningTest extends IntegrationTestSupport {
    @Mock
    ServerClock serverClock;

    @Test
    @DisplayName("jwt를 생성한다.")
    void jwt() throws Exception {
        // given
        String secretKeyPlain = "256비트 이상의 시크릿 키가 필요합니다.";
        byte[] keyBase64Encoded = Base64.getEncoder().encode(secretKeyPlain.getBytes());
        // Base64 인코딩된 키를 이용하여 SecretKey 객체를 만든다.
        SecretKey secretKey = Keys.hmacShaKeyFor(keyBase64Encoded);
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.of(LocalTime.now().getHour(), 0);
        Timestamp iat = Timestamp.valueOf(LocalDateTime.of(date,time));
        String jws = Jwts.builder()
                .setSubject("joe")
                .setIssuedAt(iat)
                .setExpiration(new Date(iat.getTime()+1000*60*60))//만료시간 - ms단위;1000=1초
                .signWith(secretKey)
                .compact();
        JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(secretKey).build();

        // when
        Claims claims = jwtParser.parseClaimsJws(jws).getBody();
        // then

        assertThat(claims.getSubject()).isEqualTo("joe");
        assertThat(claims.getIssuedAt()).isEqualTo(iat);
    }


    @Test
    @DisplayName("외부에서 Secret Key를 주입받아서 jwt를 생성한다.")
    void injectJwtKey() throws Exception {
        // given
        SecretKey secretKey = appConfig.getJwtProperties().getSecretKey();
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.of(LocalTime.now().getHour(), 0);
        Timestamp iat = Timestamp.valueOf(LocalDateTime.of(date,time));
        String jws = Jwts.builder()
                .setSubject("joe")
                .setIssuedAt(iat)
                .setExpiration(new Date(iat.getTime()+appConfig.getJwtProperties().getValidTime()))//만료시간 - ms단위;1000=1초
                .signWith(secretKey)
                .compact();

        // when
        JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(secretKey).build();

        // then
        Claims claims = jwtParser.parseClaimsJws(jws).getBody();
        assertThat(claims.getSubject()).isEqualTo("joe");
    }

    @Test
    @DisplayName("만료된 jwt의 경우 만료 예외가 발생한다.")
    void expiredJwt() throws Exception {
        // given
        SecretKey secretKey = appConfig.getJwtProperties().getSecretKey();
        LocalDate date = LocalDate.of(2000,1,1);
        LocalTime time = LocalTime.of(LocalTime.now().getHour(), 0);
        Timestamp iat = Timestamp.valueOf(LocalDateTime.of(date,time));
        String jws = Jwts.builder()
                .setSubject("joe")
                .setIssuedAt(iat)
                .setExpiration(new Date(iat.getTime()+appConfig.getJwtProperties().getValidTime()))//만료시간 - ms단위;1000=1초
                .signWith(secretKey)
                .compact();

        // when
        JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(secretKey).build();

        // then
        assertThatThrownBy(() -> jwtParser.parseClaimsJws(jws))
                .isInstanceOf(ExpiredJwtException.class);
    }
    @Test
    @DisplayName("유효하지 않은 jwt의 경우 검증 예외가 발생한다.")
    void invalidJwt() throws Exception {
        // given
        SecretKey secretKey = appConfig.getJwtProperties().getSecretKey();
        String invalidKeyPlain = "256비트 이상의 시크릿 키가 필요합니다.";
        byte[] invalidKeyBase64Encoded = Base64.getEncoder().encode(invalidKeyPlain.getBytes());
        SecretKey invalidKey = Keys.hmacShaKeyFor(invalidKeyBase64Encoded);

        Timestamp iat = Timestamp.valueOf(LocalDateTime.now());
        String jws = Jwts.builder()
                .setSubject("joe")
                .setIssuedAt(iat)
                .setExpiration(new Date(iat.getTime() + appConfig.getJwtProperties().getValidTime()))//만료시간 - ms단위;1000=1초
                .signWith(invalidKey)
                .compact();

        // when
        JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(secretKey).build();

        // then
        assertThatThrownBy(() -> jwtParser.parseClaimsJws(jws))
                .isInstanceOf(SignatureException.class);
    }

    @Test
    @DisplayName("테스트용 토큰 발급")
    void tokenForPostman() throws Exception {
        Long id = 1L;
        given(serverClock.getDateTime()).willReturn(LocalDateTime.now());
        String token = jwtManager.issueToken(id.toString());
        assertThat(jwtManager.isValid(token)).isTrue();
        System.out.println(token);

    }


}
