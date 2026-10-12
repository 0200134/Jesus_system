package com.jesus.system.api;

import com.jesus.system.core.ProcessLifecycleManager;
import com.jesus.system.core.ProcessLifecycleManager.EjectionReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Emergency manual override REST API.
 *
 * <p>Formerly {@code JesusCommandController}, which only logged the request
 * and returned a fake SUCCESS — it now actually executes the protocol and
 * returns the real ejection report.
 */
@RestController
@RequestMapping("/api/jesus")
public class WatchdogCommandController {

    private static final Logger log = LoggerFactory.getLogger(WatchdogCommandController.class);

    private final ProcessLifecycleManager lifecycleManager;

    public WatchdogCommandController(ProcessLifecycleManager lifecycleManager) {
        this.lifecycleManager = lifecycleManager;
    }

    @PostMapping("/eject-and-resurrect")
    public ResponseEntity<Map<String, Object>> manualEjectAndResurrect(
            @RequestParam(defaultValue = "admin-override") String authorityToken) {

        // Never log the token itself.
        log.warn("[⚠️ EMERGENCY] Manual ejection requested via API (token length: {})", authorityToken.length());

        EjectionReport report = lifecycleManager.ejectAndResurrect("MANUAL_API_TRIGGER");

        return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "action", "EJECT_AND_RESURRECT_EXECUTED",
                "processesTerminated", report.processesTerminated(),
                "resurrectionTriggered", report.resurrectionTriggered(),
                "timestamp", report.timestamp().toEpochMilli()
        ));
    }
}
