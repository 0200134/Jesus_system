package com.jesus.system.monitor;

import com.jesus.system.config.WatchdogProperties;
import com.jesus.system.core.ProcessLifecycleManager;
import com.jesus.system.core.VitalsRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * eGovFrame legacy composite monitor: validates DB connection pool vitals
 * (when a DataSource is configured) plus WAS HTTP responsiveness, and ejects
 * the target after sustained failures.
 *
 * <p>Formerly {@code JesusEgovWatchdogService}. The hard DataSource dependency
 * that prevented the app from booting without JDBC configuration is now an
 * {@link ObjectProvider} — the DB check simply skips when no DataSource exists.
 */
@Component
public class EgovWatchdogMonitor {

    private static final Logger log = LoggerFactory.getLogger(EgovWatchdogMonitor.class);
    private static final String COMPONENT = "egov-legacy";

    private final WatchdogProperties properties;
    private final ObjectProvider<DataSource> dataSourceProvider;
    private final HttpVitalsProbe httpProbe;
    private final VitalsRegistry vitals;
    private final ProcessLifecycleManager lifecycleManager;

    private final FailureCounter failures = new FailureCounter();

    public EgovWatchdogMonitor(WatchdogProperties properties,
                               ObjectProvider<DataSource> dataSourceProvider,
                               HttpVitalsProbe httpProbe,
                               VitalsRegistry vitals,
                               ProcessLifecycleManager lifecycleManager) {
        this.properties = properties;
        this.dataSourceProvider = dataSourceProvider;
        this.httpProbe = httpProbe;
        this.vitals = vitals;
        this.lifecycleManager = lifecycleManager;
    }

    /**
     * 2초 주기로 eGovFrame 레거시의 DB 상태와 WAS 응답성을 복합 검증
     */
    @Scheduled(fixedRate = 2000)
    public void monitorEgovSystem() {
        boolean dbAlive = checkDatabaseVitals();
        boolean httpAlive = httpProbe.isAlive(properties.getHealthEndpoint());

        if (dbAlive && httpAlive) {
            failures.reset();
            vitals.reportUp(COMPONENT, "DB and WAS vitals are nominal.");
            return;
        }

        int failureCount = failures.recordFailure();
        vitals.reportDown(COMPONENT,
                "Health check failed (dbAlive=%s, httpAlive=%s) %d/%d"
                        .formatted(dbAlive, httpAlive, failureCount, properties.getFailureThreshold()));

        if (failures.hasReached(properties.getFailureThreshold())) {
            log.warn("[🚨 CRITICAL] eGovFrame legacy unresponsive. Executing J.E.S.U.S Ejection Protocol.");
            lifecycleManager.ejectAndResurrect("EGOV_COMPOSITE_CHECK_FAILED");
            failures.reset();
        }
    }

    /**
     * DB 커넥션 풀 생체 신호 검증. DataSource가 설정되지 않은 경우 검사를 건너뛴다.
     */
    private boolean checkDatabaseVitals() {
        DataSource dataSource = dataSourceProvider.getIfAvailable();
        if (dataSource == null) {
            // No eGov datasource configured — DB layer check is skipped.
            return true;
        }
        try (Connection connection = dataSource.getConnection()) {
            return connection.isValid(2);
        } catch (Exception e) {
            log.error("[❌ DB FAIL] eGovFrame database connection lost: {}", e.getMessage());
            return false;
        }
    }
}
