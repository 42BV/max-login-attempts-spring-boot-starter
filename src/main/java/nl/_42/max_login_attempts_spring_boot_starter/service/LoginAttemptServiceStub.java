package nl._42.max_login_attempts_spring_boot_starter.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(value = "max-login-attempts-starter.enabled", havingValue = "false")
public class LoginAttemptServiceStub implements LoginAttemptService {

    @Override
    public boolean isBlocked(String username, String remoteAddress) {
        return false;
    }

    @Override
    public boolean loginFailed(String username, String remoteAddress) {
        return false;
    }

    @Override
    public void reset() {
        // do nothing
    }

    @Override
    public void loginSucceeded(String username, String remoteAddress) {
        // do nothing
    }
}
