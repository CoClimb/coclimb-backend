package swm.s3.coclimb.config.aspect.logtrace;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogTraceImpl implements LogTrace {
    private static final String START_PREFIX = "-->";
    private static final String COMPLETE_PREFIX = "<--";
    private static final String EXCEPTION_PREFIX = "<X-";
    private ThreadLocal<TraceId> traceIdHolder = new ThreadLocal<>();

    @Override
    public TraceStatus begin(String message) {
        syncTraceId();
        TraceId traceId = traceIdHolder.get();

        log.info("[{}] {}{} time = {}ms",
                traceId.getId(), addSpace(START_PREFIX, traceId.getLevel()), message);

        long startTimeMs = System.currentTimeMillis();
        return new TraceStatus(traceId, startTimeMs, message);
    }

    @Override
    public void end(TraceStatus traceStatus, Exception e) {
        long resultTimeMs = System.currentTimeMillis() - traceStatus.getStartTimeMs();

        TraceId traceId = traceStatus.getTraceId();
        String message = traceStatus.getMessage();

        if (e == null) {
            log.info("[{}] {}{} time = {}ms",
                    traceId.getId(), addSpace(COMPLETE_PREFIX, traceId.getLevel()), message, resultTimeMs);
        } else {
            log.info("[{}] {}{} time = {}ms exception = {}",
                    traceId.getId(),addSpace(EXCEPTION_PREFIX,traceId.getLevel()), message, resultTimeMs, e.toString());
        }

        releaseTraceId(traceStatus);
    }

    private void syncTraceId() {
        TraceId traceId = traceIdHolder.get();
        if (traceId == null) {
            traceIdHolder.set(new TraceId());
        } else {
            traceIdHolder.set(traceId.createNextId());
        }
    }

    private void releaseTraceId(TraceStatus traceStatus) {
        if (traceStatus.getTraceId().isFirstLevel()) {
            traceIdHolder.remove();
        }
    }

    private static String addSpace(String prefix, int level) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; i++) {
            sb.append( (i == level - 1) ? "|" + prefix : "|   ");
        }
        return sb.toString();
    }

}