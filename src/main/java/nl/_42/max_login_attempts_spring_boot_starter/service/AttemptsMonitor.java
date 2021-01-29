package nl._42.max_login_attempts_spring_boot_starter.service;

import java.time.Clock;
import java.time.LocalDateTime;

public class AttemptsMonitor {

    private int attempts;
    private LocalDateTime unblockTime;

    public AttemptsMonitor() {
        setDefaultValues();
    }

    public int getAttempts() {
        return this.attempts;
    }

    public LocalDateTime getUnblockTime() {
        return unblockTime;
    }

    public void addAttempt() {
        this.attempts += 1;
    }

    public void block(LocalDateTime unblockTime) {
        this.unblockTime = unblockTime;
    }

    public void unblock() {
        this.unblockTime = null;
    }

    public void reset() {
        setDefaultValues();
    }

    public boolean isBlocked(Clock clock) {
        return unblockTime != null && LocalDateTime.now(clock).isBefore(unblockTime);
    }

    private void setDefaultValues() {
        this.attempts = 0;
        this.unblockTime = null;
    }
}
