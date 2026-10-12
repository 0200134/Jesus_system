package com.jesus.system.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;

import java.time.Duration;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class WatchdogPropertiesTest {

    @Test
    void bindsCustomValuesFromConfiguration() {
        ConfigurationPropertySource source = new MapConfigurationPropertySource(Map.of(
                "jesus.watchdog.probe-port", "9091",
                "jesus.watchdog.heap-critical-threshold", "0.80",
                "jesus.watchdog.min-free-disk-space", "1GB",
                "jesus.watchdog.probe-timeout", "500ms",
                "jesus.watchdog.target-process-keywords", "my-legacy-app"
        ));

        WatchdogProperties properties = new Binder(source)
                .bind("jesus.watchdog", WatchdogProperties.class)
                .get(); // BindResult has no no-arg orElseThrow(); get() throws if unbound

        assertThat(properties.getProbePort()).isEqualTo(9091);
        assertThat(properties.getHeapCriticalThreshold()).isEqualTo(0.80);
        assertThat(properties.getMinFreeDiskSpace().toBytes()).isEqualTo(1024L * 1024 * 1024);
        assertThat(properties.getProbeTimeout()).isEqualTo(Duration.ofMillis(500));
        assertThat(properties.getTargetProcessKeywords()).containsExactly("my-legacy-app");
    }

    @Test
    void providesSafeDefaultsMatchingDocumentedBehavior() {
        WatchdogProperties properties = new WatchdogProperties();

        assertThat(properties.getFailureThreshold()).isEqualTo(3);
        assertThat(properties.getIoFailureThreshold()).isEqualTo(2);
        assertThat(properties.getHeapCriticalThreshold()).isEqualTo(0.92);
        assertThat(properties.getMinFreeDiskSpace().toBytes()).isEqualTo(500L * 1024 * 1024);
        assertThat(properties.getProbeHost()).isEqualTo("127.0.0.1");
        assertThat(properties.getProbePort()).isEqualTo(8080);
        assertThat(properties.getProcessCommandKeyword()).isEqualTo("java");
        assertThat(properties.getTargetProcessKeywords()).contains("egov", "tomcat");
        assertThat(properties.getNixRunTarget()).isEqualTo(".#jesus-server-17");
    }
}
