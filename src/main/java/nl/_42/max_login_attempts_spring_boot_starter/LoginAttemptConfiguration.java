package nl._42.max_login_attempts_spring_boot_starter;

import static java.util.Collections.singletonList;
import static org.springframework.http.HttpMethod.POST;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "max-login-attempts-starter")
public class LoginAttemptConfiguration {

    private boolean enabled = true;
    private int maxAttempts = 5;
    private int cooldown = 60000;
    private Integer clearAttemptsSeconds;
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

    public int getCooldown() {
        return cooldown;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public Integer getClearAttemptsSeconds() {
        return clearAttemptsSeconds;
    }

    public void setClearAttemptsSeconds(Integer clearAttemptsSeconds) {
        this.clearAttemptsSeconds = clearAttemptsSeconds;
    }

    public List<AuthenticationEndpoint> getAuthenticationEndpoints() {
        return authenticationEndpoints;
    }

    public void setAuthenticationEndpoints(List<AuthenticationEndpoint> authenticationEndpoints) {
        this.authenticationEndpoints = Collections.unmodifiableList(authenticationEndpoints);
    }
}
