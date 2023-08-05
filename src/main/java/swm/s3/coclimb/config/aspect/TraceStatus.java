package swm.s3.coclimb.config.aspect;

import lombok.Getter;

@Getter
public class TraceStatus {
    private TraceId traceId;
    private long startTimeMs;
    private String message;

    public TraceStatus(TraceId traceId, long startTimeMs, String message) {
        this.traceId = traceId;
        this.startTimeMs = startTimeMs;
        this.message = message;
    }
}
