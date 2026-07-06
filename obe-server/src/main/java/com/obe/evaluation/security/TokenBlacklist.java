package com.obe.evaluation.security;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TokenBlacklist {
    private static final Map<String, Long> blacklist = new ConcurrentHashMap<>();

    public static void add(String token) { blacklist.put(token, System.currentTimeMillis()); }
    public static boolean contains(String token) { return blacklist.containsKey(token); }
    public static void cleanExpired(long maxAgeMs) {
        long now = System.currentTimeMillis();
        blacklist.entrySet().removeIf(e -> now - e.getValue() > maxAgeMs);
    }

    @Scheduled(fixedRate = 3600000)
    public void scheduledCleanup() {
        cleanExpired(3600000);
    }
}
