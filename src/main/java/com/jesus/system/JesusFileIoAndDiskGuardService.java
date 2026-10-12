package com.jesus.system;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
publiic class JesusFileIoAndDiskGuardService {

    private static final long MIN_REQUIRED_FREE_SPACE_BYTES = 500L * 1024 * 1024; // 최소 500MB 여유 공간 강제 확보
    private int ioFailureCount = 0;
    private static final int IO_FAILURE_THRESHOLD = 2; // 2회 연속 I/O 거부 시 즉시 사출

    /**
     * 10초 주기로 디스크 잔여 용량 및 파일시스템 Read-Only(I/O 락) 상태 검증
     */
    @Scheduled(fixedRate = 10000)
    public void guardFileIoAndDisk() {
        checkDiskSpaceAndReadOnlyState();
    }

    private void checkDiskSpaceAndReadOnlyState() {
        try {
            File rootDir = new File(".");
            long usableSpace = rootDir.getUsableSpace();

            // 1. 디스크 용량 고갈 검사 (Disk Full 방어)
            if (usableSpace < MIN_REQUIRED_FREE_SPACE_BYTES) {
                System.err.println("[⚠️ DISK SPACE WARNING] Free space dangerously low: " + (usableSpace / (1024 * 1024)) + " MB");
                triggerEmergencyEjection("DISK_SPACE_EXHAUSTED");
                return;
            }

            // 2. 파일 시스템 I/O 무결성 테스트 (디스크 오류로 인한 Read-Only 잠김 탐지)
            Path tempFile = Files.createTempFile("jesus-io-probe-", ".tmp");
            try (FileWriter writer = new FileWriter(tempFile.toFile())) {
                writer.write("vitals-ok");
                ioFailureCount = 0; // 정상 통과 시 카운터 초기화
            } finally {
                Files.deleteIfExists(tempFile);
            }

        } catch (Exception e) {
            ioFailureCount++;
            System.err.println("[❌ I/O BLOCK ERROR] File system write failed or locked! Count: " + ioFailureCount + "/" + IO_FAILURE_THRESHOLD);
            
            if (ioFailureCount >= IO_FAILURE_THRESHOLD) {
                System.err.println("[🚨 CRITICAL STORAGE LOCK] File system is unresponsive or read-only. Executing J.E.S.U.S Ejection.");
                triggerEmergencyEjection("FILE_SYSTEM_IO_DEADLOCK");
                ioFailureCount = 0;
            }
        }
    }

    /**
     * 3. 스토리지 장애 감지 시 레거시 정밀 사출 및 Nix 순정 부활
     */
    private void triggerEmergencyEjection(String reason) {
        ProcessHandle.allProcesses()
            .filter(p -> {
                String cmd = p.info().command().orElse("").toLowerCase();
                String args = String.join(" ", p.info().arguments().orElse(new String[0])).toLowerCase();
                return cmd.contains("java") && (args.contains("egov") || args.contains("tomcat"));
            })
            .forEach(process -> {
                long pid = process.pid();
                System.out.println("[🗡️ STORAGE EJECT] Terminating locked PID: " + pid + " [Reason: " + reason + "]");
                process.destroyForcibly();
            });

        // Nix 환경을 통한 즉각적인 클린 재스폰
        try {
            System.out.println("[⚡ RESURRECT] Respawning fresh environment via Nix...");
            ProcessBuilder pb = new ProcessBuilder("nix", "run", ".#jesus-server-17");
            pb.start();
        } catch (Exception ex) {
            System.err.println("[💥 CRITICAL] Storage recovery resurrection failed: " + ex.getMessage());
        }
    }
}
