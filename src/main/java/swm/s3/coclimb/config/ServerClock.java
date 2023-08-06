package swm.s3.coclimb.config;

import org.springframework.stereotype.Component;
import swm.s3.coclimb.config.aspect.logtrace.NoLog;

import java.time.LocalDateTime;
@NoLog
@Component
public class ServerClock {
    public LocalDateTime getDateTime() {
        return LocalDateTime.now();
    }
}
