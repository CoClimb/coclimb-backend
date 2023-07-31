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
    private final AppConfig appConfig;
    private final ServerClock serverClock;
    private final JwtParser jwtParser;

    public JwtManager(AppConfig appConfig, ServerClock serverClock) {
        this.appConfig = appConfig;
        this.serverClock = serverClock;
        this.jwtParser = Jwts.parserBuilder().setSigningKey(appConfig.getSecretKey()).build();
    }

    public String issueToken(String subject) {
        Timestamp iat = Timestamp.valueOf(serverClock.getDateTime());
        return Jwts.builder()
                .setIssuer("CoClimb")
                .setSubject(subject)
                .setAudience("all")
                .setIssuedAt(iat)
                .setExpiration(new Date(iat.getTime() + appConfig.getValidTime()))//만료시간 - ms단위;1000=1초
                .signWith(appConfig.getSecretKey())
                .compact();
    }


    public boolean isValid(String jwt) {
        return jwtParser.isSigned(jwt);
    }

    public String getSubject(String jwt) {
        try {
            return jwtParser.parseClaimsJws(jwt).getBody().getSubject();
        } catch (Exception e) {
            throw new InvalidToken();
        }
    }
}
