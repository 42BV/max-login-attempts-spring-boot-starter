package nl._42.max_login_attempts_spring_boot_starter.service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

import nl._42.max_login_attempts_spring_boot_starter.LoginAttemptConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(value = "max-login-attempts-starter.enabled", matchIfMissing = true)
public class LoginAttemptServiceImplementation implements LoginAttemptService {

    public static final String DEFAULT_CLEAR_ALL_ATTEMPTS_CRON = "'0 0 0 * * *'";

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginAttemptServiceImplementation.class);

    private final LoginAttemptConfiguration loginAttemptConfiguration;
    private final Clock clock;

    private final ConcurrentHashMap<UsernameIPAddress, Integer> attemptsCache;
    private final ConcurrentHashMap<UsernameIPAddress, LocalDateTime> blockedUsernameIPAddresses;

    @Autowired
    public LoginAttemptServiceImplementation(LoginAttemptConfiguration loginAttemptConfiguration, Clock clock) {
        this.loginAttemptConfiguration = loginAttemptConfiguration;
        this.clock = clock;

        this.attemptsCache = new ConcurrentHashMap<>();
        this.blockedUsernameIPAddresses = new ConcurrentHashMap<>();
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
        LOGGER.debug("User {} on remote address {} used incorrect login credentials. Added a counter to the login attempts.", username, remoteAddress);

        // If the remoteAddress is already blocked we don't need to do anything and return.
        if (isBlocked(username, remoteAddress)) {
            LOGGER.debug("User {} on remote address {} was already blocked, no additional failure is added.", username, remoteAddress);
            return true;
        }

        UsernameIPAddress usernameIPAddress = new UsernameIPAddress(username, remoteAddress);

        int attempts = attemptsCache.getOrDefault(usernameIPAddress, 0);
        attemptsCache.put(usernameIPAddress, attempts + 1);

        /*
         * If the remoteAddress is now blocked we mark it for removal
         * but we clear the attempts so the counter starts to re-run
         * when the unblock deadline has passed.
         */
        if (hasReachedLoginAttemptLimit(username, remoteAddress)) {
            int attemptLimit = loginAttemptConfiguration.getMaxAttempts();
            LocalDateTime unblockTime = LocalDateTime.now(clock).plusSeconds(loginAttemptConfiguration.getCooldown() / 1000);

            LOGGER.warn("User {} on remote address {} has reached the login attempt limit of {} and is now blocked until {}.",  username, remoteAddress, attemptLimit, unblockTime);
            clearAttempts(usernameIPAddress);
            blockedUsernameIPAddresses.put(usernameIPAddress, unblockTime);
            return true;
        }

        return false;
    }

    private boolean hasReachedLoginAttemptLimit(String username, String remoteAddress) {
        UsernameIPAddress usernameIPAddress = new UsernameIPAddress(username, remoteAddress);
        int attempts = attemptsCache.getOrDefault(usernameIPAddress, 0);
        return attempts >= loginAttemptConfiguration.getMaxAttempts();
    }

    /**
     * Marks a login attempt as successful, clearing the previous
     * amount of failed attempts (if any).
     * @param remoteAddress remote internet address
     */
    @Override
    public void loginSucceeded(String username, String remoteAddress) {
        LOGGER.debug("User {} on remote address {} succeeded to login.", username, remoteAddress);
        UsernameIPAddress usernameIPAddress = new UsernameIPAddress(username, remoteAddress);
        clearAttempts(usernameIPAddress);
    }

    private void clearAttempts(UsernameIPAddress usernameIPAddress) {
        attemptsCache.remove(usernameIPAddress);
    }

    /**
     * Returns true if the remote address is currently blocked.
     * @param remoteAddress remote internet address
     * @return whether or not the remote address is blocked
     */
    @Override
    public synchronized boolean isBlocked(String username, String remoteAddress) {
        UsernameIPAddress usernameIPAddress = new UsernameIPAddress(username, remoteAddress);

        /*
         * If the username/ip-address combination is not currently in the
         * blockedUsernameIPAddresses map we are sure it is not blocked.
         */
        if (!blockedUsernameIPAddresses.containsKey(usernameIPAddress)) {
            return false;
        }

        // Otherwise we retrieve the unblockTime and check if it has passed.
        // If it has passed we do a little cleanup and remove the record.
        LocalDateTime unblockTime = blockedUsernameIPAddresses.get(usernameIPAddress);
        if (LocalDateTime.now(clock).isBefore(unblockTime)) {
            LOGGER.debug("User {} on remote address {} cannot log in because he is blocked until {}", username, remoteAddress, unblockTime);
            return true;
        } else {
            LOGGER.debug("User {} on remote address {} may login again because the unblock time of {} has passed.", unblockTime, remoteAddress, unblockTime);
            blockedUsernameIPAddresses.remove(usernameIPAddress);
            return false;
        }
    }

    /**
     * Completely reset all attempts.
     * Can be executed manually and also be scheduled to reset automatically.
     */
    @Override
    @Scheduled(cron = "${max-login-attempts-starter.clear-all-attempts-cron:#{" + DEFAULT_CLEAR_ALL_ATTEMPTS_CRON + "}}")
    public synchronized void reset() {
        LOGGER.info("Clearing all login attempt records.");
        attemptsCache.clear();
        blockedUsernameIPAddresses.clear();
    }
}
