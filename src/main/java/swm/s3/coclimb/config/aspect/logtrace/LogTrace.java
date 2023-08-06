package swm.s3.coclimb.config.aspect.logtrace;

public interface LogTrace {

    TraceStatus begin(String message);
    void end(TraceStatus traceStatus, Exception e);

}