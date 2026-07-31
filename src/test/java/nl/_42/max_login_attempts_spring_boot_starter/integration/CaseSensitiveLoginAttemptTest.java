package nl._42.max_login_attempts_spring_boot_starter.integration;

import static org.assertj.core.api.Assertions.assertThat;

import nl._42.max_login_attempts_spring_boot_starter.AbstractSpringTest;
import nl._42.max_login_attempts_spring_boot_starter.service.LoginAttemptService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = "max-login-attempts-starter.case-sensitive-usernames=true")
class CaseSensitiveLoginAttemptTest extends AbstractSpringTest {

    private static final String REMOTE_ADDRESS = "127.0.0.1";

    @Autowired
    private LoginAttemptService loginAttemptService;

    @Test
    void differentCasings_shouldBeTrackedAsDifferentUsers() {
        block("Admin");

        assertThat(loginAttemptService.isBlocked("Admin", REMOTE_ADDRESS)).isTrue();
        assertThat(loginAttemptService.isBlocked("admin", REMOTE_ADDRESS)).isFalse();
    }

    @Test
    void resetByUsername_shouldOnlyClearTheExactCasing() {
        block("Admin");

        loginAttemptService.resetByUsername("admin");
        assertThat(loginAttemptService.isBlocked("Admin", REMOTE_ADDRESS)).isTrue();

        loginAttemptService.resetByUsername("Admin");
        assertThat(loginAttemptService.isBlocked("Admin", REMOTE_ADDRESS)).isFalse();
    }

    private void block(String username) {
        for (int attempt = 0; attempt < 3; attempt++) {
            loginAttemptService.loginFailed(username, REMOTE_ADDRESS);
        }
    }
}
