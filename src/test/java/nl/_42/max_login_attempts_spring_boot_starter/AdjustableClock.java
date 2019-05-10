package nl._42.max_login_attempts_spring_boot_starter;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class AdjustableClock extends Clock {

    private Clock clock;

    public AdjustableClock() {
        reset();
    }

    public void reset() {
        this.clock = Clock.systemDefaultZone();
    }

    public void setTime(LocalDateTime time) {
        Instant instant = ZonedDateTime.of(time, ZoneId.systemDefault()).toInstant();
        this.clock = Clock.fixed(instant, ZoneId.systemDefault());
    }

    @Override
    public ZoneId getZone() {
        return clock.getZone();
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return clock.withZone(zone);
    }

    @Override
    public Instant instant() {
        return clock.instant();
    }
}
