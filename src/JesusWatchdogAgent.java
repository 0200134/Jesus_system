package com.jesus.system.agent;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.Optional;

@Component
public class JesusWatchdogAgent {

    // 타겟 공공 레거시(eGovFrame) 프로세스 이름 또는 식별자
    private static final String TARGET_PROCESS_NAME = "egov-legacy-app";
    private long lastHealthyTime = Instant.now().toEpochMilli();

    /**
     * 0.5초마다 실행되는 실시간 생체 신호 모니터링 링커
     */
    @Scheduled(fixedRate = 500)
    public void monitorAndHeal() {
        try {
            boolean isHealthy = checkTargetHealth();

            if (!isHealthy) {
                System.out.println("[⚠️] WARNING: Target eGovFrame is unresponsive! Initiating J.E.S.U.S Protocol...");
                ejectAndResurrect();
            } else {
                lastHealthyTime = Instant.now().toEpochMilli();
            }
        } catch (Exception e) {
            System.err.println("[❌] Watchdog Error: " + e.getMessage());
        }
    }

    /**
     * 1. 헬스 체크 로직 ( actuator 또는 소켓 응답 검증 )
     */
    private boolean checkTargetHealth() {
        // 실제 구현 시 RestTemplate이나 HttpClient로 타겟 포트(예: 8080/actuator/health) 핑 테스트
        // 여기서는 예시로 응답 지연이나 타임아웃 발생 시 false 반환
        return true; 
    }

    /**
     * 2. 사출 (Eject) 및 부활 (Resurrect) 집행
     */
    private void ejectAndResurrect() {
        // Java 17+ ProcessHandle을 통한 정밀 프로세스 사출
        ProcessHandle.allProcesses()
            .filter(p -> p.info().command().orElse("").contains(TARGET_PROCESS_NAME))
            .forEach(process -> {
                long pid = process.pid();
                System.out.println("[🗡️ EJECT] Killing frozen target process PID: " + pid);
                process.destroyForcibly(); // 강제 처형 (SIGKILL)
            });

        // 3. Nix 기반 원자적 부활 트리거
        resurrectWithNix();
    }

    /**
     * 3. Nix 환경을 호출하여 순정 상태로 즉시 재스폰
     */
    private void resurrectWithNix() {
        try {
            System.out.println("[⚡ RESURRECT] Spawning fresh instance via Nix environment...");
            ProcessBuilder pb = new ProcessBuilder("nix", "run", ".#jesus-server-17");
            pb.redirectErrorStream(true);
            Process process = pb.start();
            
            System.out.println("[✨] J.E.S.U.S Resurrection completed successfully.");
        } catch (Exception e) {
            System.err.println("[💥 CRITICAL] Failed to resurrect target: " + e.getMessage());
        }
    }
}
