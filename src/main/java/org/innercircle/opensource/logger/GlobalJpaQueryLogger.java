package org.innercircle.opensource.logger;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.innercircle.opensource.context.GlobalJpaQueryContext;
import org.innercircle.opensource.util.JpaQueryLoggerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Aspect
@Component
@ConditionalOnProperty(name = "my.jpa.query.logger.enabled", havingValue = "true")
public class GlobalJpaQueryLogger {

    private static final Logger log = LoggerFactory.getLogger(GlobalJpaQueryLogger.class);

    private final GlobalJpaQueryContext globalJpaQueryContext;

    @Value("${my.jpa.query.logger.global-query-measurement-time-ms:600000}")
    private long MEASUREMENT_TIME_MS;

    @Value("${my.jpa.query.logger.global-query-invoke-count-threshold:100}")
    private long INVOKED_QUERY_THRESHOLD;

    public GlobalJpaQueryLogger(GlobalJpaQueryContext globalJpaQueryContext) {
        this.globalJpaQueryContext = globalJpaQueryContext;
    }

    @Around("execution(* org.springframework.data.repository.CrudRepository+.*(..))")
    public Object measureExecutionCountInCertainTimes(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = JpaQueryLoggerUtils.extractFullMethodName(joinPoint);
        globalJpaQueryContext.addMethodCall(methodName);

        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        int callCountInLastHour = globalJpaQueryContext.getCallCountInLastHour(methodName, startTime, MEASUREMENT_TIME_MS);
        if (callCountInLastHour > INVOKED_QUERY_THRESHOLD) {
            log.info(
                "⚠️ High usage detected: {} called {} times in the last {} hour {} minutes!, invoked method threshold count: {}",
                methodName,
                callCountInLastHour,
                MEASUREMENT_TIME_MS / 1000 / 60,
                MEASUREMENT_TIME_MS / 1000,
                INVOKED_QUERY_THRESHOLD);
        }
        return result;
    }

}
