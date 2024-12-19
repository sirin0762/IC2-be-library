package org.innercircle.opensource.context;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@ConditionalOnProperty(name = "my.jpa.query.logger.enabled", havingValue = "true")
public class GlobalJpaQueryContext {

    private final Map<String, List<Long>> methodCallTimestamps = new ConcurrentHashMap<>();

    @Value("${my.jpa.query.logger.global-query-measurement-time-ms:600000}")
    private long CLEANUP_INTERVAL;

    public void addMethodCall(String methodKey) {
        methodCallTimestamps
            .computeIfAbsent(methodKey, key -> new CopyOnWriteArrayList<>())
            .add(System.currentTimeMillis());
    }

    public int getCallCountInLastHour(String methodKey, long currentTime, long timeWindowMillis) {
        List<Long> timestamps = methodCallTimestamps.get(methodKey);
        if (timestamps == null) {
            return 0;
        }
        return (int) timestamps.stream()
            .filter(timestamp -> currentTime - timestamp <= timeWindowMillis)
            .count();
    }

    public void reset() {
        methodCallTimestamps.clear();
    }

    @PostConstruct
    public void startCleanupSchedulerIfNeeded() {
        if (CLEANUP_INTERVAL > 0) {
            startCleanupScheduler();
        }
    }

    public Map<String, Integer> getAllCallCounts(long currentTime, long timeWindowMillis) {
        Map<String, Integer> callCounts = new HashMap<>();
        methodCallTimestamps.forEach((key, timestamps) -> {
            int count = (int) timestamps.stream()
                .filter(timestamp -> currentTime - timestamp <= CLEANUP_INTERVAL)
                .count();
            callCounts.put(key, count);
        });
        return callCounts;
    }

    private void startCleanupScheduler() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            long currentTime = System.currentTimeMillis();

            methodCallTimestamps.forEach((methodKey, timestamps) -> {
                timestamps.removeIf(timestamp -> currentTime - timestamp > CLEANUP_INTERVAL);
            });

        }, CLEANUP_INTERVAL, CLEANUP_INTERVAL, TimeUnit.MILLISECONDS);
    }

}
