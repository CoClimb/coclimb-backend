package swm.s3.coclimb.config.security;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;
import swm.s3.coclimb.api.exception.errortype.login.InvalidToken;
import swm.s3.coclimb.config.AppConfig;
import swm.s3.coclimb.config.ServerClock;

import java.sql.Timestamp;
import java.util.Date;

@Component
public class JwtManager {
//    private final AppConfig appConfig;
    private final ServerClock serverClock;
    private final JwtParser jwtParser;
    private final JwtProperties jwt;

    public JwtManager(AppConfig appConfig, ServerClock serverClock) {
        jwt = appConfig.getJwtProperties();
        this.serverClock = serverClock;
        this.jwtParser = Jwts.parserBuilder().setSigningKey(jwt.getSecretKey()).build();
    }

    public String issueToken(String subject) {
        Timestamp iat = Timestamp.valueOf(serverClock.getDateTime());
        return Jwts.builder()
                .setIssuer("CoClimb")
                .setSubject(subject)
                .setAudience("all")
                .setIssuedAt(iat)
                .setExpiration(new Date(iat.getTime() + jwt.getValidTime()))//만료시간 - ms단위;1000=1초
                .signWith(jwt.getSecretKey())
                .compact();
    }


    public boolean isValid(String jwt) {
        try {
            jwtParser.parseClaimsJws(jwt);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public String getSubject(String jwt) {
        try {
            return jwtParser.parseClaimsJws(jwt).getBody().getSubject();
        } catch (Exception e) {
            throw new InvalidToken();
        }
    }
}
