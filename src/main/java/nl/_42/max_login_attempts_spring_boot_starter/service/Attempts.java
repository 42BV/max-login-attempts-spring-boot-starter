package nl._42.max_login_attempts_spring_boot_starter.service;

import java.time.Clock;
import java.time.LocalDateTime;

public class Attempts {
    private final Clock clock;
    private Integer totalAttempts;
    private LocalDateTime lastAttemptTime;

    public Attempts(Clock clock) {
        this.totalAttempts = 0;
        this.lastAttemptTime = LocalDateTime.now(clock);
        this.clock = clock;
    }

    public Integer getTotalAttempts() {
        return totalAttempts;
    }

    public LocalDateTime getLastAttemptTime() {
        return lastAttemptTime;
    }

    public Attempts addAttempt() {
        this.totalAttempts += 1;
        this.lastAttemptTime = LocalDateTime.now(clock);
        return this;
    }
}