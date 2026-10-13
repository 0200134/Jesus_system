package com.jesus.system.monitor;

final class FailureCounter {

    private int consecutiveFailures;

    int recordFailure() {
        return ++consecutiveFailures;
    }

    int recoverOne() {
        consecutiveFailures = Math.max(0, consecutiveFailures - 1);
        return consecutiveFailures;
    }

    boolean hasReached(int threshold) {
        return consecutiveFailures >= threshold;
    }

    void reset() {
        consecutiveFailures = 0;
    }
}
