package nl._42.max_login_attempts_spring_boot_starter.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(value = "max-login-attempts-starter.enabled", havingValue = "false")
public class LoginAttemptServiceStub implements LoginAttemptService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginAttemptServiceStub.class);

    @Autowired
    public LoginAttemptServiceStub() {
        LOGGER.info("The LoginAttemptServiceStub is being used, which means that login attempts are not actually limited.");
    }

    @Override
    public boolean isBlocked(String username, String remoteAddress) {
        return false;
    }

    @Override
    public boolean loginFailed(String username, String remoteAddress) {
        return false;
    }

    @Override
    public void resetByUsername(String username) {
        // do nothing
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
