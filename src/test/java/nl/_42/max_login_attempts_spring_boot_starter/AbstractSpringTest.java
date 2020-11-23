package nl._42.max_login_attempts_spring_boot_starter;

import nl._42.max_login_attempts_spring_boot_starter.service.LoginAttemptService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = LoginAttemptService.class)
@Import({ TestConfiguration.class })
@ActiveProfiles({ "unit-test", "disable-scheduling" })
public abstract class AbstractSpringTest {

    @Autowired
    protected LoginAttemptService loginAttemptService;

    @BeforeEach
    void clearLoginAttempts() {
        loginAttemptService.reset();
    }
}