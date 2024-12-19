package org.innercircle.opensource.logger;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.innercircle.opensource.context.LocalJpaQueryContext;
import org.innercircle.opensource.context.dto.LocalQueryCounter;
import org.innercircle.opensource.util.JpaQueryLoggerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Map;

@Aspect
@Component
@ConditionalOnProperty(name = "my.jpa.query.logger.enabled", havingValue = "true")
public class LocalJpaQueryLogger {

    private static final Logger log = LoggerFactory.getLogger(LocalJpaQueryLogger.class);

    @Value( "${my.jpa.query.logger.local-query-threshold:3000}")
    private long SLOW_QUERY_THRESHOLD;

    @Around("execution(* org.springframework.data.repository.CrudRepository+.*(..))")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = JpaQueryLoggerUtils.extractFullMethodName(joinPoint);
        long startTime = System.currentTimeMillis();

        LocalJpaQueryContext.setLocalQueryCounter(new LocalQueryCounter(methodName, startTime));

        Object result = joinPoint.proceed();

        LocalQueryCounter queryCount = LocalJpaQueryContext.getLocalQueryCounter();

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - queryCount.getStartTime();

        if (executionTime > SLOW_QUERY_THRESHOLD) {
            log.info("⚠️ Slow jpa method detected: {} took {} ms", queryCount.getMethodName(), executionTime);
        }

        for(Map.Entry<String, Integer> queryResult: queryCount.getQueryCounter().entrySet()) {
            log.info("Invoked query in method - invoked count: {}, invoked query string: {}", queryResult.getValue(), queryResult.getKey());
        }

        return result;
    }

}
