package nl._42.max_login_attempts_spring_boot_starter.service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import nl._42.max_login_attempts_spring_boot_starter.LoginAttemptConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Primary
@Service
@ConditionalOnProperty(value = "max-login-attempts-starter.enabled", matchIfMissing = true)
public class LoginAttemptServiceImplementation implements LoginAttemptService {

    private final Logger log = LoggerFactory.getLogger(LoginAttemptServiceImplementation.class);
    private final LoginAttemptConfiguration loginAttemptConfiguration;
    private final Clock clock;

    private final ConcurrentHashMap<UsernameIPAddress, AttemptsMonitor> userCache;

    public LoginAttemptServiceImplementation(LoginAttemptConfiguration loginAttemptConfiguration, Clock clock) {
        this.loginAttemptConfiguration = loginAttemptConfiguration;
        this.clock = clock;

        this.userCache = new ConcurrentHashMap<>();
    }

    /**
     * Marks a failed login attempt for the remote address.
     * A counter is kept to keep track of the amount of failed logins
     * and if this surpasses the maximum amount of times configured in
     * the properties file, the user is locked out until  the cooldown
     * time surpasses.
     *
     * The mechanism uses a ConcurrentHashMap to keep track of the amount
     * of tries, and a PriorityBlockingQueue to easily traverse through
     * the entries that are marked for unblocking in the correct order.
     *
     * @param remoteAddress remote internet address
     * @return boolean - whether or not the user is (now) blocked.
     */
    @Override
    public synchronized boolean loginFailed(String username, String remoteAddress) {
        log.debug("User {} on remote address {} used incorrect login credentials. Added a counter to the login attempts.", username, remoteAddress);

        // If the remoteAddress is already blocked we don't need to do anything and return.
        if (isBlocked(username, remoteAddress)) {
            log.debug("User {} on remote address {} was already blocked, no additional failure is added.", username, remoteAddress);
            return true;
        }

        UsernameIPAddress usernameIPAddress = new UsernameIPAddress(username, remoteAddress);

        AttemptsMonitor attemptsMonitor = getAttemptsMonitor(usernameIPAddress);
        attemptsMonitor.addAttempt();
        userCache.putIfAbsent(usernameIPAddress, attemptsMonitor);

        /*
         * If the remoteAddress is now blocked we mark it for removal
         * but we clear the attempts so the counter starts to re-run
         * when the unblock deadline has passed.
         */
        if (hasReachedLoginAttemptLimit(attemptsMonitor)) {
            int attemptLimit = loginAttemptConfiguration.getMaxAttempts();
            LocalDateTime unblockTime = LocalDateTime.now(clock).plusSeconds(loginAttemptConfiguration.getCooldownInMs() / 1000);

            log.warn("User {} on remote address {} has reached the login attempt limit of {} and is now blocked until {}.",  username, remoteAddress, attemptLimit, unblockTime);
            attemptsMonitor.reset();
            attemptsMonitor.block(unblockTime);
            return true;
        }

        return false;
    }

    private boolean hasReachedLoginAttemptLimit(AttemptsMonitor attemptsMonitor) {
        return attemptsMonitor.getAttempts() >= loginAttemptConfiguration.getMaxAttempts();
    }

    /**
     * Marks a login attempt as successful, clearing the previous
     * amount of failed attempts (if any).
     * @param remoteAddress remote internet address
     */
    @Override
    public void loginSucceeded(String username, String remoteAddress) {
        log.debug("User {} on remote address {} succeeded to login.", username, remoteAddress);
        UsernameIPAddress usernameIPAddress = new UsernameIPAddress(username, remoteAddress);
        getAttemptsMonitor(usernameIPAddress).reset();
    }

    private AttemptsMonitor getAttemptsMonitor(UsernameIPAddress usernameIPAddress) {
        return userCache.getOrDefault(usernameIPAddress, new AttemptsMonitor());
    }

    /**
     * Returns true if the remote address is currently blocked.
     * @param remoteAddress remote internet address
     * @return whether or not the remote address is blocked
     */
    @Override
    public synchronized boolean isBlocked(String username, String remoteAddress) {
        UsernameIPAddress usernameIPAddress = new UsernameIPAddress(username, remoteAddress);

        // Otherwise we retrieve the unblockTime and check if it has passed.
        // If it has passed we do a little cleanup and remove the record.
        AttemptsMonitor attemptsMonitor = getAttemptsMonitor(usernameIPAddress);
        LocalDateTime unblockTime = attemptsMonitor.getUnblockTime();
        if (attemptsMonitor.isBlocked(clock)) {
            log.debug("User {} on remote address {} cannot log in because he is blocked until {}", username, remoteAddress, unblockTime);
            return true;
        } else {
            log.debug("User {} on remote address {} may login again because the unblock time of {} has passed.", unblockTime, remoteAddress, unblockTime);
            attemptsMonitor.unblock();
            return false;
        }
    }

    @Override
    public synchronized void resetByUsername(String username) {
        log.info("Clearing login attempt records of user {}.", username);
        Optional<Map.Entry<UsernameIPAddress, AttemptsMonitor>> entryAttempt = userCache.entrySet().stream()
                .filter(k -> k.getKey().getUsername().equals(username))
                .findFirst();

        entryAttempt.ifPresent(entry -> getAttemptsMonitor(entry.getKey()).reset());
    }

    /**
     * Completely reset all attempts.
     * Can be executed manually and also be scheduled to reset automatically.
     */
    @Override
    @Scheduled(cron = "#{@loginAttemptConfiguration.clearAllAttemptsCron}")
    public synchronized void reset() {
        log.info("Clearing all login attempt records.");
        userCache.clear();
    }
}
