package swm.s3.coclimb.config.aspect.logtrace;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;

@Slf4j
@Aspect
@Order(1)
public class LogTraceAspect {

    private final LogTrace logTrace;

    public LogTraceAspect(LogTrace logTrace) {
        this.logTrace = logTrace;
    }

    @Around("all() && exceptFinal() && exceptConfig()")
    public Object traceLog(ProceedingJoinPoint joinPoint) throws Throwable {

        TraceStatus traceStatus = logTrace.begin(joinPoint.getSignature().toShortString());
        try {
            Object result = joinPoint.proceed();
            logTrace.end(traceStatus,null);
            return result;
        } catch (Exception e) {
            logTrace.end(traceStatus,e);
            throw e;
        }
    }

    @Pointcut("execution(* swm.s3.coclimb..*(..))")
    private void all(){}
    @Pointcut("execution(* swm.s3.coclimb.api..*(..))")
    private void api(){}
    @Pointcut("!execution(* swm.s3.coclimb.api.adapter.out.instagram.InstagramOAuthRecord..*(..))")
    private void exceptFinal(){}

    @Pointcut("!execution(* swm.s3.coclimb.config.*Config..*(..))")
    private void exceptConfig(){}
}
