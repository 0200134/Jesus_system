package com.jesus.system.monitor;

import com.jesus.system.config.WatchdogProperties;
import com.jesus.system.core.ProcessLifecycleManager;
import com.jesus.system.core.VitalsRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Primary 0.5s watchdog loop: pings the target's actuator health endpoint
 * and executes the eject-and-resurrect protocol after sustained failures.
 *
 * <p>Formerly {@code JesusWatchdogAgent}; the stub {@code checkTargetHealth()}
 * that always returned {@code true} is now a real HTTP vitals check.
 */
@Component
public class WatchdogAgent {

    private static final Logger log = LoggerFactory.getLogger(WatchdogAgent.class);
    private static final String COMPONENT = "actuator-ping";

    private final WatchdogProperties properties;
    private final HttpVitalsProbe httpProbe;
    private final VitalsRegistry vitals;
    private final ProcessLifecycleManager lifecycleManager;

    private int consecutiveFailures;

    public WatchdogAgent(WatchdogProperties properties,
                         HttpVitalsProbe httpProbe,
                         VitalsRegistry vitals,
                         ProcessLifecycleManager lifecycleManager) {
        this.properties = properties;
        this.httpProbe = httpProbe;
        this.vitals = vitals;
        this.lifecycleManager = lifecycleManager;
    }

    /**
     * 0.5초마다 실행되는 실시간 생체 신호 모니터링 루프
     */
    @Scheduled(fixedRate = 500)
    public void monitorAndHeal() {
        try {
            if (httpProbe.isAlive(properties.getHealthEndpoint())) {
                consecutiveFailures = 0;
                vitals.reportUp(COMPONENT, "Target actuator endpoint is responsive.");
                return;
            }

            int failures = ++consecutiveFailures;
            vitals.reportDown(COMPONENT,
                    "Health endpoint unreachable (%d/%d)".formatted(failures, properties.getFailureThreshold()));

            if (failures >= properties.getFailureThreshold()) {
                log.warn("[⚠️] Target unresponsive after {} checks. Initiating J.E.S.U.S Protocol...", failures);
                lifecycleManager.ejectAndResurrect("TARGET_ACTUATOR_UNRESPONSIVE");
                consecutiveFailures = 0;
            }
        } catch (Exception e) {
            log.error("[❌] Watchdog error: {}", e.getMessage(), e);
        }
    }
}
