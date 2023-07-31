package swm.s3.coclimb.config;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ServerClock {
    public LocalDateTime getDateTime() {
        return LocalDateTime.now();
    }
}
