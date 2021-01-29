package nl._42.max_login_attempts_spring_boot_starter;

import static java.util.Collections.singletonList;
import static org.springframework.http.HttpMethod.POST;

import java.util.Collections;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("max-login-attempts-starter")
public class LoginAttemptConfiguration {

    /**
     * Enable or disable the max-login-attempts library.
     */
    private boolean enabled = true;

    /**
     * Sets the total attempts a user can incorrectly login.
     */
    private int maxAttempts = 5;

    /**
     * Sets the cooldown time of when a user is blocked.
     */
    private int cooldownInMs = 60000;

    /**
     * Cron for clearing all users from the attempts cache.
     */
    private String clearAllAttemptsCron = "0 0 0 * * *";

    /**
     * Sets the cooldown time of when a user is blocked.
     */
    private List<AuthenticationEndpoint> authenticationEndpoints = singletonList(
            AuthenticationEndpoint.create("/authentication", POST)
    );

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public int getCooldownInMs() {
        return cooldownInMs;
    }

    public void setCooldownInMs(int cooldownInMs) {
        this.cooldownInMs = cooldownInMs;
    }

    public String getClearAllAttemptsCron() {
        return clearAllAttemptsCron;
    }

    public void setClearAllAttemptsCron(String clearAllAttemptsCron) {
        this.clearAllAttemptsCron = clearAllAttemptsCron;
    }

    public List<AuthenticationEndpoint> getAuthenticationEndpoints() {
        return authenticationEndpoints;
    }

    public void setAuthenticationEndpoints(List<AuthenticationEndpoint> authenticationEndpoints) {
        this.authenticationEndpoints = Collections.unmodifiableList(authenticationEndpoints);
    }
}
