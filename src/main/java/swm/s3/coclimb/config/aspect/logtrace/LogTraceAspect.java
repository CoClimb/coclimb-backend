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

    @Around("all() && !isFinal() && !isConfig() && !hasNoLog(NoLog)")
    public Object traceLog(ProceedingJoinPoint joinPoint) throws Throwable {

        TraceStatus traceStatus = logTrace.begin(joinPoint.getSignature().toShortString(),joinPoint.getArgs());
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
//    @Pointcut("execution(* swm.s3.coclimb.api..*(..))")
//    private void api(){}
    @Pointcut("target(swm.s3.coclimb.api.adapter.out.instagram.InstagramOAuthRecord)")
    private void isFinal(){}
    @Pointcut("within(swm.s3.coclimb.config.*Config)")
    private void isConfig(){}
    @Pointcut("@annotation(noLog) || @target(noLog)")
    private void hasNoLog(NoLog noLog) {}
//    @Pointcut("execution(* swm.s3.coclimb..*JpaRepository.*(..))")
//    private void isJpaRepository(){}

}
