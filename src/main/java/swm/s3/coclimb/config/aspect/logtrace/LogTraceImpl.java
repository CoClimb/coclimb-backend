package swm.s3.coclimb.config.aspect.logtrace;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogTraceImpl implements LogTrace {
    private static final String START_PREFIX = "-->";
    private static final String COMPLETE_PREFIX = "<--";
    private static final String EXCEPTION_PREFIX = "<X-";
    private ThreadLocal<TraceId> traceIdHolder = new ThreadLocal<>();

    @Override
    public TraceStatus begin(String message, Object[] args) {
        syncTraceId();
        TraceId traceId = traceIdHolder.get();

        String argsJson = convertArrayToJson(args);
        log.info("[{}] {}{} args = {}",
                traceId.getId(), addSpace(START_PREFIX, traceId.getLevel()), message, argsJson);

        long startTimeMs = System.currentTimeMillis();
        return new TraceStatus(traceId, startTimeMs, message);
    }

    @Override
    public void end(TraceStatus traceStatus, Exception e) {
        if (e == null) {
            writeSuccessLog(traceStatus);
        } else {
            writeExceptionLog(traceStatus, e);
        }
        releaseTraceId();
    }

    public void writeSuccessLog(TraceStatus traceStatus) {
        long resultTimeMs = System.currentTimeMillis() - traceStatus.getStartTimeMs();

        TraceId traceId = traceStatus.getTraceId();
        String message = traceStatus.getMessage();

        if (resultTimeMs < 1000) {
            log.info("[{}] {}{} time = {}ms",
                    traceId.getId(), addSpace(COMPLETE_PREFIX, traceId.getLevel()), message, resultTimeMs);
        }else{
            log.warn("[{}] {}{} time = {}ms",
                    traceId.getId(), addSpace(COMPLETE_PREFIX, traceId.getLevel()), message, resultTimeMs);
        }
    }
    public void writeExceptionLog(TraceStatus traceStatus, Exception e) {
        long resultTimeMs = System.currentTimeMillis() - traceStatus.getStartTimeMs();

        TraceId traceId = traceStatus.getTraceId();
        String message = traceStatus.getMessage();

        if (resultTimeMs < 1000) {
            log.info("[{}] {}{} time = {}ms exception = {}",
                    traceId.getId(),addSpace(EXCEPTION_PREFIX,traceId.getLevel()), message, resultTimeMs, e.toString());
        } else {
            log.warn("[{}] {}{} time = {}ms exception = {}",
                    traceId.getId(),addSpace(EXCEPTION_PREFIX,traceId.getLevel()), message, resultTimeMs, e.toString());
        }
    }

    private void syncTraceId() {
        TraceId traceId = traceIdHolder.get();
        if (traceId == null) {
            traceIdHolder.set(new TraceId());
        } else {
            traceIdHolder.set(traceId.createNextId());
        }
    }

    private void releaseTraceId() {
        TraceId traceId = traceIdHolder.get();
        if (traceId.isFirstLevel()) {
            traceIdHolder.remove();
        }else{
            traceIdHolder.set(traceId.createPreviousId());
        }
    }

    private static String addSpace(String prefix, int level) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; i++) {
            sb.append( (i == level - 1) ? "|" + prefix : "|   ");
        }
        return sb.toString();
    }

    private String convertArrayToJson(Object[] array) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
//            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            return objectMapper.writeValueAsString(array);
        } catch (Exception e) {
            // Handle JSON conversion exception (if needed)
            return "Error converting array to JSON";
        }
    }
}