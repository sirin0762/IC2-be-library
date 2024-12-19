package org.innercircle.opensource.context.dto;

import java.util.HashMap;
import java.util.Map;

public class LocalQueryCounter {

    private final String methodName;

    private final long startTime;

    private final Map<String, Integer> queryCounter = new HashMap<>();

    public LocalQueryCounter(String methodName, long startTime) {
        this.methodName = methodName;
        this.startTime = startTime;
    }

    public void appendQuery(String sql) {
        queryCounter.putIfAbsent(sql, 0);
        queryCounter.put(sql, queryCounter.get(sql) + 1);
    }

    public String getMethodName() {
        return methodName;
    }

    public long getStartTime() {
        return startTime;
    }

    public Map<String, Integer> getQueryCounter() {
        return queryCounter;
    }

}
