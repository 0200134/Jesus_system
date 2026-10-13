package com.jesus.system.monitor;

import com.jesus.system.config.WatchdogProperties;
import com.jesus.system.core.ProcessLifecycleManager;
import com.jesus.system.core.VitalsRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.lang.management.ThreadMXBean;

/**
 * Deep JVM diagnostics: preemptively detects heap exhaustion and thread
 * deadlocks before the target GC locks up completely.
 *
 * <p>Formerly {@code JesusJvmDiagnosticsService}.
 */
@Component
public class JvmDiagnosticsMonitor {

    private static final Logger log = LoggerFactory.getLogger(JvmDiagnosticsMonitor.class);
    private static final String COMPONENT = "jvm-diagnostics";

    private final WatchdogProperties properties;
    private final VitalsRegistry vitals;
    private final ProcessLifecycleManager lifecycleManager;

    private final FailureCounter heapPressureFailures = new FailureCounter();

    public JvmDiagnosticsMonitor(WatchdogProperties properties,
                                 VitalsRegistry vitals,
                                 ProcessLifecycleManager lifecycleManager) {
        this.properties = properties;
        this.vitals = vitals;
        this.lifecycleManager = lifecycleManager;
    }

    /**
     * 5초 주기로 JVM 내부 메모리 압박 상태와 쓰레드 락 상태를 정밀 진단
     */
    @Scheduled(fixedRate = 5000)
    public void diagnoseJvmHealth() {
        checkHeapMemoryPressure();
        checkThreadDeadlocks();
    }

    /**
     * 힙 메모리 누수(OOM 임박) 선제 감지
     */
    private void checkHeapMemoryPressure() {
        MemoryUsage heapUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        long maxMemory = heapUsage.getMax();
        if (maxMemory <= 0) {
            return;
        }

        double usageRatio = (double) heapUsage.getUsed() / maxMemory;
        if (usageRatio >= properties.getHeapCriticalThreshold()) {
            int count = heapPressureFailures.recordFailure();
            vitals.reportDown(COMPONENT,
                    "Heap usage at %.1f%% (%d/%d)".formatted(usageRatio * 100, count, properties.getFailureThreshold()));

            if (heapPressureFailures.hasReached(properties.getFailureThreshold())) {
                log.warn("[🚨 CRITICAL OOM PREEMPTION] Heap exhausted. Preemptively triggering ejection.");
                lifecycleManager.ejectAndResurrect("JVM_HEAP_MEMORY_OVERFLOW");
                heapPressureFailures.reset();
            }
        } else {
            // Recover gradually instead of resetting immediately.
            if (heapPressureFailures.recoverOne() == 0) {
                vitals.reportUp(COMPONENT, "Heap usage at %.1f%%".formatted(usageRatio * 100));
            }
        }
    }

    /**
     * 쓰레드 데드락(Deadlock) 실시간 포착
     */
    private void checkThreadDeadlocks() {
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        long[] deadlockedThreads = threadMXBean.findDeadlockedThreads();

        if (deadlockedThreads != null && deadlockedThreads.length > 0) {
            vitals.reportDown(COMPONENT, deadlockedThreads.length + " deadlocked threads detected");
            log.error("[💀 DEADLOCK DETECTED] Found {} deadlocked threads!", deadlockedThreads.length);
            lifecycleManager.ejectAndResurrect("THREAD_DEADLOCK_CRITICAL");
        }
    }
}
