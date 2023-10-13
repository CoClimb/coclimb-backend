package swm.s3.coclimb.config.security;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;
import swm.s3.coclimb.api.exception.errortype.login.InvalidToken;
import swm.s3.coclimb.config.AppConfig;
import swm.s3.coclimb.config.ServerClock;
import swm.s3.coclimb.config.propeties.JwtProperties;

import java.sql.Timestamp;
import java.util.Date;

@Component
public class JwtManager {
//    private final AppConfig appConfig;
    private final ServerClock serverClock;
    private final JwtParser jwtParser;
    private final JwtProperties jwtProperties;

    public JwtManager(AppConfig appConfig, ServerClock serverClock) {
        jwtProperties = appConfig.getJwtProperties();
        this.serverClock = serverClock;
        this.jwtParser = Jwts.parserBuilder().setSigningKey(jwtProperties.getSecretKey()).build();
    }

    public String issueToken(String subject) {
        Timestamp iat = Timestamp.valueOf(serverClock.getDateTime());
        return Jwts.builder()
                .setIssuer("CoClimb")
                .setSubject(subject)
                .setAudience("all")
                .setIssuedAt(iat)
                .setExpiration(new Date(iat.getTime() + jwtProperties.getValidTime()))//만료시간 - ms단위;1000=1초
                .signWith(jwtProperties.getSecretKey())
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
