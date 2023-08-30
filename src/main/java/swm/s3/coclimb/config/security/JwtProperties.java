package swm.s3.coclimb.config.security;

import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.crypto.SecretKey;
import java.util.Base64;

@Getter
@ConfigurationProperties(prefix = "app-config.jwt")
public class JwtProperties {

    private final byte[] jwtKey;
    private final long validTime;
    private final SecretKey secretKey;

    public JwtProperties(String jwtKey, String validTime) {
        this.jwtKey = Base64.getDecoder().decode(jwtKey);
        this.secretKey = Keys.hmacShaKeyFor(this.jwtKey);
        this.validTime = Long.parseLong(validTime) * 1000;
    }

}
