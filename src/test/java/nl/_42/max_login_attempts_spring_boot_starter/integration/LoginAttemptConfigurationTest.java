package nl._42.max_login_attempts_spring_boot_starter.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import nl._42.max_login_attempts_spring_boot_starter.AbstractWebIntegrationTest;
import nl._42.max_login_attempts_spring_boot_starter.LoginAttemptConfiguration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class LoginAttemptConfigurationTest extends AbstractWebIntegrationTest {

    @Autowired
    private LoginAttemptConfiguration loginAttemptConfiguration;

    @Test
    void applicationPropertiesCorrectyMapped() {
        assertEquals(3, loginAttemptConfiguration.getMaxAttempts());
        assertEquals(60001, loginAttemptConfiguration.getCooldownInMs());
        assertEquals("0 */1 * * *", loginAttemptConfiguration.getClearAllAttemptsCron());
    }
}
