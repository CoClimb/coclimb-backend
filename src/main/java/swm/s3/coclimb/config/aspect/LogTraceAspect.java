package swm.s3.coclimb.config.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.util.UUID;

@Slf4j
@Aspect
public class LogTraceAspect {

    @Pointcut("execution(* swm.s3.coclimb..*(..))")
    private void all(){}
    @Pointcut("execution(* swm.s3.coclimb.api..*(..))")
    private void api(){}
    @Pointcut("!execution(* swm.s3.coclimb.api.adapter.out.instagram.InstagramOAuthRecord..*(..))")
    private void exceptFinal(){}
    @Pointcut("!execution(* swm.s3.coclimb.config.*Config..*(..))")
    private void exceptConfig(){}

    @Around("all() && exceptFinal() && exceptConfig()")
    public Object traceLog(ProceedingJoinPoint joinPoint) throws Throwable {
        String logId = UUID.randomUUID().toString().substring(0,8);

        log.info("[{}] begin = {}",logId,joinPoint.getSignature().toShortString());
        long startAt = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        long resultTime = System.currentTimeMillis()-startAt;
        log.info("[{}] end = {}, time = {}ms",logId,joinPoint.getSignature().toShortString(),resultTime);

        return result;
    }
}
