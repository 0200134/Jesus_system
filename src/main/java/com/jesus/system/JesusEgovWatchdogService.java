package com.jesus.system;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.time.Instant;

@Component
public class JesusEgovWatchdogService {

    // eGovFrame 표준 실행 프로세스 키워드 (tomcat, java, war 파일명 등)
    private static final String EGOV_PROCESS_KEYWORD = "java"; 
    private static final String EGOV_APP_IDENTIFIER = "egov"; // 실행 중인 jar/war 또는 톰캣 경로 내 키워드
    private static final String EGOV_HEALTH_ENDPOINT = "http://localhost:8080/actuator/health"; // 또는 레거시 웰컴 페이지

    private final DataSource dataSource; // eGovFrame의 핵심 DB 커넥션 주입 (스프링 자동 주입)
    private int consecutiveFailures = 0;
    private static final int FAILURE_THRESHOLD = 3; // 3번 연속 실패 시에만 사출 집행 (오탐 방지)

    public JesusEgovWatchdogService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * 2초 주기로 eGovFrame 레거시의 DB 상태와 WAS 응답성을 복합 검증
     */
    @Scheduled(fixedRate = 2000)
    public void monitorEgovSystem() {
        boolean isDbAlive = checkDatabaseVitals();
        boolean isHttpAlive = checkHttpVitals();

        if (isDbAlive && isHttpAlive) {
            // 정상 상태일 경우 실패 카운터 초기화
            consecutiveFailures = 0;
        } else {
            consecutiveFailures++;
            System.err.println("[⚠️ eGov-Watchdog] Health check failed! Count: " + consecutiveFailures + "/" + FAILURE_THRESHOLD);

            if (consecutiveFailures >= FAILURE_THRESHOLD) {
                System.err.println("[🚨 CRITICAL] eGovFrame legacy is unresponsive. Executing J.E.S.U.S Ejection Protocol.");
                ejectAndResurrectEgov();
                consecutiveFailures = 0; // 리셋
            }
        }
    }

    /**
     * 1. DB 커넥션 풀 생체 신호 검증 (eGovFrame 핵심 연동)
     */
    private boolean checkDatabaseVitals() {
        try (Connection conn = dataSource.getConnection()) {
            return conn.isValid(2); // 2초 이내 유효성 응답 확인
        } catch (Exception e) {
            System.err.println("[❌ DB FAIL] eGovFrame Database connection lost: " + e.getMessage());
            return false;
        }
    }

    /**
     * 2. HTTP / WAS 응답성 검증
     */
    private boolean checkHttpVitals() {
        try {
            URL url = new URL(EGOV_HEALTH_ENDPOINT);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(1500);
            connection.setReadTimeout(1500);
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            return (responseCode >= 200 && responseCode < 500);
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 3. eGovFrame 프로세스 정밀 사출 및 Nix 부활
     */
    private void ejectAndResurrectEgov() {
        ProcessHandle.allProcesses()
            .filter(p -> {
                String cmd = p.info().command().orElse("").toLowerCase();
                String args = String.join(" ", p.info().arguments().orElse(new String[0])).toLowerCase();
                // java 프로세스이면서 eGovFrame 관련 아티팩트나 톰캣을 구동 중인 프로세스 타겟팅
                return cmd.contains(EGOV_PROCESS_KEYWORD) && (args.contains(EGOV_APP_IDENTIFIER) || args.contains("tomcat"));
            })
            .forEach(process -> {
                long pid = process.pid();
                System.out.println("[🗡️ eGov-EJECT] Forcibly terminating frozen eGovFrame PID: " + pid);
                process.destroyForcibly();
            });

        // Nix 환경을 통한 순정 상태 재스폰
        try {
            System.out.println("[⚡ eGov-RESURRECT] Triggering Nix build & run pipeline...");
            ProcessBuilder pb = new ProcessBuilder("nix", "run", ".#jesus-server-17");
            pb.start();
        } catch (Exception e) {
            System.err.println("[💥 CRITICAL] Failed to respawn eGovFrame via Nix: " + e.getMessage());
        }
    }
}
