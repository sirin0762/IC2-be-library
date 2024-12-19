package org.innercircle.opensource.util;

import org.aspectj.lang.ProceedingJoinPoint;

import java.util.Arrays;

public class JpaQueryLoggerUtils {

    public static String extractFullMethodName(ProceedingJoinPoint joinPoint) {
        String repositoryInterface = Arrays.stream(joinPoint.getTarget().getClass().getInterfaces())
            .map(Class::getSimpleName)
            .filter(name -> !name.startsWith("JpaRepository"))
            .findFirst()
            .orElse("UnknownRepository");
        String methodName = joinPoint.getSignature().getName();

        return repositoryInterface + "." + methodName;
    }

}
