package com.jesus.system.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Shared registry where every monitor reports its latest vital sign.
 * Aggregated by the actuator health indicator so /actuator/health
 * reflects the real state of each surveillance layer.
 */
@Component
public class VitalsRegistry {

    private static final Logger log = LoggerFactory.getLogger(VitalsRegistry.class);

    public enum Status {
        UP, DOWN
    }

    public record Vital(Status status, String message, java.time.Instant updatedAt) {
    }

    private final Map<String, Vital> vitals = new ConcurrentHashMap<>();

    public void reportUp(String component, String message) {
        report(component, Status.UP, message);
    }

    public void reportDown(String component, String message) {
        report(component, Status.DOWN, message);
    }

    private void report(String component, Status status, String message) {
        vitals.put(component, new Vital(status, message, java.time.Instant.now()));
        if (status == Status.DOWN) {
            log.warn("[VITALS] {} is DOWN: {}", component, message);
        }
    }

    public Map<String, Vital> snapshot() {
        return Map.copyOf(vitals);
    }

    public boolean hasFailures() {
        return vitals.values().stream().anyMatch(vital -> vital.status() == Status.DOWN);
    }
}
