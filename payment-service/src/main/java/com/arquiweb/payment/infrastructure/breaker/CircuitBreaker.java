package com.arquiweb.payment.infrastructure.breaker;

import com.arquiweb.payment.infrastructure.breaker.enums.BreakerStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CircuitBreaker {

    private BreakerStatus currentState = BreakerStatus.CLOSED;
    private int failureCount = 0;
    private final int failureThreshold = 3;
    private LocalDateTime openUntilTime;

    public synchronized boolean allowRequest() {
        if (currentState == BreakerStatus.OPEN) {
            if (LocalDateTime.now().isAfter(openUntilTime)) {
                currentState = BreakerStatus.HALF_OPEN;
                return true;
            }
            return false;
        }
        return true;
    }

    public synchronized void recordSuccess() {
        failureCount = 0;
        this.currentState =BreakerStatus.CLOSED;
    }

    public synchronized void recordFailure() {
        failureCount++;
        if (failureCount >= failureThreshold || currentState == BreakerStatus.HALF_OPEN) {
            currentState = BreakerStatus.OPEN;
            openUntilTime = LocalDateTime.now().plusSeconds(10);
        }
    }
}


