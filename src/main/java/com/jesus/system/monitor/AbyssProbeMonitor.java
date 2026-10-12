package com.jesus.system.monitor;

import com.jesus.system.config.WatchdogProperties;
import com.jesus.system.core.VitalsRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Low-level raw TCP socket probe (detection-only).
 *
 * <p>Bypasses HTTP overhead and directly checks whether the legacy port
 * still accepts TCP handshakes. Reports into the vitals registry so
 * /actuator/health reflects socket-level freezes; ejection is left to
 * the threshold-based monitors.
 *
 * <p>Formerly {@code JesusAbyssProbeComponent}.
 */
@Component
public class AbyssProbeMonitor {

    private static final Logger log = LoggerFactory.getLogger(AbyssProbeMonitor.class);
    private static final String COMPONENT = "raw-socket-probe";

    private final WatchdogProperties properties;
    private final VitalsRegistry vitals;

    public AbyssProbeMonitor(WatchdogProperties properties, VitalsRegistry vitals) {
        this.properties = properties;
        this.vitals = vitals;
    }

    @Scheduled(fixedRate = 1000)
    public void probeRawSocket() {
        try (Socket socket = new Socket()) {
            socket.connect(
                    new InetSocketAddress(properties.getProbeHost(), properties.getProbePort()),
                    (int) properties.getProbeTimeout().toMillis());
            vitals.reportUp(COMPONENT, "TCP handshake OK on port " + properties.getProbePort());
        } catch (IOException e) {
            String message = "Raw socket handshake failed on port " + properties.getProbePort();
            vitals.reportDown(COMPONENT, message);
            log.warn("[☠️ ABYSS ALERT] {} — target may be deadlocked.", message);
        }
    }
}
