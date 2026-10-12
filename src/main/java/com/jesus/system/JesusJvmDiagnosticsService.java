package com.jesus.system;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.lang.management.ThreadMXBean;

@Component
public class JesusJvmDiagnosticsService {

    private static final double HEAP_CRITICAL_THRESHOLD = 0.92; // 힙 메모리 92% 점유 시 위험 신호
    private int heapOverflowCount = 0;
    private static final int THRESHOLD_COUNT = 3; // 3회 연속 감지 시 선제 사출

    /**
     * 5초 주기로 JVM 내부 메모리 압박 상태와 쓰레드 락 상태를 정밀 진단
     */
    @Scheduled(fixedRate = 5000)
    public void diagnoseJvmHealth() {
        checkHeapMemoryPressure();
        checkThreadDeadlocks();
    }

    /**
     * 1. 힙 메모리 누수(OOM 임박) 선제 감지
     */
    private void checkHeapMemoryPressure() {
        MemoryUsage heapUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        long maxMemory = heapUsage.getMax();
        long usedMemory = heapUsage.getUsed();

        if (maxMemory > 0) {
            double usageRatio = (double) usedMemory / maxMemory;
            
            if (usageRatio >= HEAP_CRITICAL_THRESHOLD) {
                heapOverflowCount++;
                System.err.println("[⚠️ JVM HEAP WARNING] Heap usage at " + String.format("%.1f", usageRatio * 100) + "%! Count: " + heapOverflowCount + "/" + THRESHOLD_COUNT);

                if (heapOverflowCount >= THRESHOLD_COUNT) {
                    System.err.println("[🚨 CRITICAL OOM PREEMPTION] Heap exhausted. Preemptively triggering J.E.S.U.S Ejection.");
                    triggerPreemptiveEjection("JVM_HEAP_MEMORY_OVERFLOW");
                    heapOverflowCount = 0;
                }
            } else {
                // 정상 범위로 복구되면 카운터 점진적 감소
                heapOverflowCount = Math.max(0, heapOverflowCount - 1);
            }
        }
    }

    /**
     * 2. 쓰레드 데드락(Deadlock) 실시간 포착
     */
    private void checkThreadDeadlocks() {
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        long[] deadlockedThreads = threadMXBean.findDeadlockedThreads();

        if (deadlockedThreads != null && deadlockedThreads.length > 0) {
            System.err.println("[💀 DEADLOCK DETECTED] Found " + deadlockedThreads.length + " deadlocked threads in eGovFrame!");
            triggerPreemptiveEjection("THREAD_DEADLOCK_CRITICAL");
        }
    }

    /**
     * 3. 장애 확산 전 선제적 사출 및 Nix 순정 부활 집행
     */
    private void triggerPreemptiveEjection(String reason) {
        ProcessHandle.allProcesses()
            .filter(p -> {
                String cmd = p.info().command().orElse("").toLowerCase();
                String args = String.join(" ", p.info().arguments().orElse(new String[0])).toLowerCase();
                return cmd.contains("java") && (args.contains("egov") || args.contains("tomcat"));
            })
            .forEach(process -> {
                long pid = process.pid();
                System.out.println("[🗡️ PREEMPTIVE EJECT] Terminating leaking/deadlocked PID: " + pid + " [Reason: " + reason + "]");
                process.destroyForcibly();
            });

        // Nix 환경을 통한 즉각적인 클린 재스폰
        try {
            System.out.println("[⚡ RESURRECT] Spawning fresh, unpolluted instance via Nix...");
            ProcessBuilder pb = new ProcessBuilder("nix", "run", ".#jesus-server-17");
            pb.start();
        } catch (Exception e) {
            System.err.println("[💥 CRITICAL] Preemptive resurrection failed: " + e.getMessage());
        }
    }
}
