package com.jesus.system.monitor;

import com.jesus.system.config.WatchdogProperties;
import com.jesus.system.core.ProcessLifecycleManager;
import com.jesus.system.core.VitalsRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Disk &amp; I/O guard: detects disk space exhaustion and file system
 * read-only lockups (I/O deadlocks) before they take the legacy target down.
 *
 * <p>Formerly {@code JesusFileIoAndDiskGuardService}.
 */
@Component
public class DiskIoGuardMonitor {

    private static final Logger log = LoggerFactory.getLogger(DiskIoGuardMonitor.class);
    private static final String COMPONENT = "disk-io-guard";

    private final WatchdogProperties properties;
    private final VitalsRegistry vitals;
    private final ProcessLifecycleManager lifecycleManager;

    private int ioFailureCount;

    public DiskIoGuardMonitor(WatchdogProperties properties,
                              VitalsRegistry vitals,
                              ProcessLifecycleManager lifecycleManager) {
        this.properties = properties;
        this.vitals = vitals;
        this.lifecycleManager = lifecycleManager;
    }

    /**
     * 10초 주기로 디스크 잔여 용량 및 파일시스템 Read-Only(I/O 락) 상태 검증
     */
    @Scheduled(fixedRate = 10000)
    public void guardFileIoAndDisk() {
        long usableSpace = new File(".").getUsableSpace();

        // 1. 디스크 용량 고갈 검사 (Disk Full 방어)
        if (usableSpace < properties.getMinFreeDiskSpace().toBytes()) {
            vitals.reportDown(COMPONENT, "Free space critically low: " + usableSpace / (1024 * 1024) + " MB");
            log.error("[⚠️ DISK SPACE WARNING] Free space dangerously low: {} MB", usableSpace / (1024 * 1024));
            lifecycleManager.ejectAndResurrect("DISK_SPACE_EXHAUSTED");
            return;
        }

        // 2. 파일 시스템 I/O 무결성 테스트 (Read-Only 잠김 탐지)
        try {
            Path probeFile = Files.createTempFile("jesus-io-probe-", ".tmp");
            try (FileWriter writer = new FileWriter(probeFile.toFile())) {
                writer.write("vitals-ok");
            } finally {
                Files.deleteIfExists(probeFile);
            }
            ioFailureCount = 0;
            vitals.reportUp(COMPONENT, "Disk space and file system writes are nominal.");
        } catch (Exception e) {
            int failures = ++ioFailureCount;
            vitals.reportDown(COMPONENT,
                    "File system write failed (%d/%d)".formatted(failures, properties.getIoFailureThreshold()));
            log.error("[❌ I/O BLOCK ERROR] File system write failed or locked ({}/{})",
                    failures, properties.getIoFailureThreshold());

            if (failures >= properties.getIoFailureThreshold()) {
                log.error("[🚨 CRITICAL STORAGE LOCK] File system unresponsive or read-only. Executing ejection.");
                lifecycleManager.ejectAndResurrect("FILE_SYSTEM_IO_DEADLOCK");
                ioFailureCount = 0;
            }
        }
    }
}
