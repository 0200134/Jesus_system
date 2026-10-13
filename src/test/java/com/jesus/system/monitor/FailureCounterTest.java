package com.jesus.system.monitor;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FailureCounterTest {

    @Test
    void reachesConfiguredThresholdAndCanBeReset() {
        FailureCounter failures = new FailureCounter();

        assertThat(failures.recordFailure()).isEqualTo(1);
        assertThat(failures.hasReached(2)).isFalse();
        assertThat(failures.recordFailure()).isEqualTo(2);
        assertThat(failures.hasReached(2)).isTrue();

        failures.reset();

        assertThat(failures.recordFailure()).isEqualTo(1);
        assertThat(failures.hasReached(2)).isFalse();
    }

    @Test
    void gradualRecoveryNeverDropsBelowZero() {
        FailureCounter failures = new FailureCounter();
        failures.recordFailure();
        failures.recordFailure();

        assertThat(failures.recoverOne()).isEqualTo(1);
        assertThat(failures.recoverOne()).isZero();
        assertThat(failures.recoverOne()).isZero();
        assertThat(failures.hasReached(3)).isFalse();
    }
}
