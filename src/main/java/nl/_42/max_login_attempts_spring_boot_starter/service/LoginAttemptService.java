package nl._42.max_login_attempts_spring_boot_starter.service;

/**
 * Service which keeps track of login attempts performed from a given remote address.
 */
public interface LoginAttemptService {

    boolean loginFailed(String username, String remoteAddress);

    void loginSucceeded(String username, String remoteAddress);

    boolean isBlocked(String username, String remoteAddress);

    void reset();
}
