package nl._42.max_login_attempts_spring_boot_starter.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import nl._42.max_login_attempts_spring_boot_starter.AbstractWebIntegrationTest;
import nl._42.max_login_attempts_spring_boot_starter.service.LoginAttemptService;
import nl._42.restsecure.autoconfigure.form.LoginForm;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

class LoginAttemptResetTest extends AbstractWebIntegrationTest {

    private static final String DEFAULT_REMOTE_ADDRESS = "127.0.0.1";
    private static final String OTHER_REMOTE_ADDRESS = "127.0.0.2";

    @Autowired
    private LoginAttemptService loginAttemptService;

    @Test
    void resetByUsername_shouldClearAllEntriesOfUser_regardlessOfCasingAndRemoteAddress() throws Exception {
        // Block the user on two remote addresses, typing the username with different casings.
        blockUser("ADMIN", DEFAULT_REMOTE_ADDRESS);
        blockUser("Admin", OTHER_REMOTE_ADDRESS);

        // The block also holds for the lowercase username, even with the correct password.
        login("admin", "welkom", DEFAULT_REMOTE_ADDRESS)
            .andExpect(MockMvcResultMatchers.status().isForbidden())
            .andExpect(jsonPath("errorCode").value("TOO_MANY_LOGIN_ATTEMPTS"));

        loginAttemptService.resetByUsername(" Admin ");

        // The reset clears the records of every remote address, whatever the casing was.
        login("admin", "welkom", DEFAULT_REMOTE_ADDRESS)
            .andExpect(MockMvcResultMatchers.status().isOk());
        assertThat(loginAttemptService.isBlocked("admin", OTHER_REMOTE_ADDRESS)).isFalse();
    }

    @Test
    void successfulLogin_shouldClearFailedAttempts() throws Exception {
        // Two failed attempts: one below the limit of three.
        login("admin", "niet-welkom", DEFAULT_REMOTE_ADDRESS)
            .andExpect(MockMvcResultMatchers.status().isUnauthorized());
        login("admin", "niet-welkom", DEFAULT_REMOTE_ADDRESS)
            .andExpect(MockMvcResultMatchers.status().isUnauthorized());

        login("admin", "welkom", DEFAULT_REMOTE_ADDRESS)
            .andExpect(MockMvcResultMatchers.status().isOk());

        // The successful login cleared the counter, so two more failed attempts do not add up
        // to the earlier two and the user is not blocked.
        login("admin", "niet-welkom", DEFAULT_REMOTE_ADDRESS)
            .andExpect(MockMvcResultMatchers.status().isUnauthorized());
        login("admin", "niet-welkom", DEFAULT_REMOTE_ADDRESS)
            .andExpect(MockMvcResultMatchers.status().isUnauthorized());

        login("admin", "welkom", DEFAULT_REMOTE_ADDRESS)
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    private void blockUser(String username, String remoteAddress) throws Exception {
        for (int attempt = 0; attempt < 3; attempt++) {
            login(username, "niet-welkom", remoteAddress);
        }
        assertThat(loginAttemptService.isBlocked(username, remoteAddress)).isTrue();
    }

    private ResultActions login(String username, String password, String remoteAddress) throws Exception {
        LoginForm loginForm = new LoginForm();
        loginForm.username = username;
        loginForm.password = password;

        return webClient
            .perform(MockMvcRequestBuilders.post("/authentication")
                .content(objectMapper.writeValueAsString(loginForm))
                .with(mockHttpServletRequest -> {
                    mockHttpServletRequest.setRemoteAddr(remoteAddress);
                    return mockHttpServletRequest;
                }));
    }
}
