package com.jesus.system.monitor;

import com.jesus.system.config.WatchdogProperties;
import com.jesus.system.core.ProcessLifecycleManager;
import com.jesus.system.core.VitalsRegistry;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class DiskIoGuardMonitorTest {

    private final WatchdogProperties properties = new WatchdogProperties();
    private final VitalsRegistry vitals = new VitalsRegistry();
    private final ProcessLifecycleManager lifecycleManager = mock(ProcessLifecycleManager.class);
    private final DiskIoGuardMonitor monitor =
            new DiskIoGuardMonitor(properties, vitals, lifecycleManager);

    @Test
    void triggersOnlyOnceWhileDiskSpaceRemainsBelowThreshold() {
        long lowSpace = properties.getMinFreeDiskSpace().toBytes() - 1;

        assertThat(monitor.checkDiskSpace(lowSpace)).isFalse();
        assertThat(monitor.checkDiskSpace(lowSpace)).isFalse();
        assertThat(vitals.hasFailures()).isTrue();
        verify(lifecycleManager).ejectAndResurrect("DISK_SPACE_EXHAUSTED");

        assertThat(monitor.checkDiskSpace(properties.getMinFreeDiskSpace().toBytes())).isTrue();
        assertThat(monitor.checkDiskSpace(lowSpace)).isFalse();
        verify(lifecycleManager, times(2)).ejectAndResurrect("DISK_SPACE_EXHAUSTED");
    }
}
