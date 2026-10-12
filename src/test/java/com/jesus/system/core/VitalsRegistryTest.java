package com.jesus.system.core;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VitalsRegistryTest {

    @Test
    void reportsUpAndDownPerComponent() {
        VitalsRegistry registry = new VitalsRegistry();

        registry.reportUp("actuator-ping", "responsive");
        assertThat(registry.hasFailures()).isFalse();

        registry.reportDown("disk-io-guard", "write failed");
        assertThat(registry.hasFailures()).isTrue();
        assertThat(registry.snapshot()).containsKeys("actuator-ping", "disk-io-guard");
        assertThat(registry.snapshot().get("disk-io-guard").status())
                .isEqualTo(VitalsRegistry.Status.DOWN);
        assertThat(registry.snapshot().get("disk-io-guard").message())
                .isEqualTo("write failed");
        assertThat(registry.snapshot().get("disk-io-guard").updatedAt()).isNotNull();
    }

    @Test
    void recoveryClearsFailure() {
        VitalsRegistry registry = new VitalsRegistry();

        registry.reportDown("jvm-diagnostics", "heap at 95%");
        assertThat(registry.hasFailures()).isTrue();

        registry.reportUp("jvm-diagnostics", "heap recovered");
        assertThat(registry.hasFailures()).isFalse();
    }

    @Test
    void snapshotIsImmutableCopy() {
        VitalsRegistry registry = new VitalsRegistry();
        registry.reportUp("a", "ok");

        assertThat(registry.snapshot()).isUnmodifiable();
    }
}
