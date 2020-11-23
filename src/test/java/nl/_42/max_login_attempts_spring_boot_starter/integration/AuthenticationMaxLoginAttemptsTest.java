package nl._42.max_login_attempts_spring_boot_starter.integration;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDateTime;

import nl._42.max_login_attempts_spring_boot_starter.AbstractWebIntegrationTest;
import nl._42.max_login_attempts_spring_boot_starter.AdjustableClock;
import nl._42.restsecure.autoconfigure.RestAuthenticationFilter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

class AuthenticationMaxLoginAttemptsTest extends AbstractWebIntegrationTest {

    @Autowired
    private AdjustableClock adjustableClock;

    @Test
    void loginAndLogoutLog() throws Exception {
        // The current time is January 1st, 2020, 00:00:00
        adjustableClock.setTime(LocalDateTime.of(2020, 1, 1, 0, 0, 0));

        RestAuthenticationFilter.LoginForm incorrectLoginForm = new RestAuthenticationFilter.LoginForm();
        incorrectLoginForm.username = "admin";
        incorrectLoginForm.password = "niet-welkom-1337";

        RestAuthenticationFilter.LoginForm correctLoginForm = new RestAuthenticationFilter.LoginForm();
        correctLoginForm.username = "admin";
        correctLoginForm.password = "welkom";

        RestAuthenticationFilter.LoginForm pietLoginForm = new RestAuthenticationFilter.LoginForm();
        pietLoginForm.username = "other-user";
        pietLoginForm.password = "niet-welkom";

        // The first two attempts are 'free'.
        webClient
                .perform(MockMvcRequestBuilders.post("/authentication")
                        .content(objectMapper.writeValueAsString(incorrectLoginForm)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());

        webClient
                .perform(MockMvcRequestBuilders.post("/authentication")
                        .content(objectMapper.writeValueAsString(incorrectLoginForm)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());

        // At the third incorrect attempt we tried too many times and we are blocked, for the 'Jan' username,
        // but not for the 'Piet' account.
        webClient
                .perform(MockMvcRequestBuilders.post("/authentication")
                        .content(objectMapper.writeValueAsString(incorrectLoginForm)))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(jsonPath("errorCode").value("TOO_MANY_LOGIN_ATTEMPTS"));

        webClient
                .perform(MockMvcRequestBuilders.post("/authentication")
                        .content(objectMapper.writeValueAsString(pietLoginForm)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());

        // We can also still attempt a login with 'Jan' from another IP.
        webClient
                .perform(MockMvcRequestBuilders.post("/authentication")
                        .content(objectMapper.writeValueAsString(incorrectLoginForm))
                        .with(mockHttpServletRequest -> {
                            mockHttpServletRequest.setRemoteAddr("127.0.0.2");
                            return mockHttpServletRequest;
                        }))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());

        // The cooldown time is 1 minute, so we set it just before that minute and try again with the correct credentials, but we are already blocked.
        adjustableClock.setTime(LocalDateTime.of(2020, 1, 1, 0, 0, 59));

        webClient
                .perform(MockMvcRequestBuilders.post("/authentication")
                        .content(objectMapper.writeValueAsString(correctLoginForm)))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(jsonPath("errorCode").value("TOO_MANY_LOGIN_ATTEMPTS"));

        // Then we fast-forward time to the point the cache is void and we try again
        adjustableClock.setTime(LocalDateTime.of(2020, 1, 1, 0, 1, 1));

        webClient
                .perform(MockMvcRequestBuilders.post("/authentication")
                        .content(objectMapper.writeValueAsString(correctLoginForm)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void resetByUsername() throws Exception {
        RestAuthenticationFilter.LoginForm incorrectLoginForm = new RestAuthenticationFilter.LoginForm();
        incorrectLoginForm.username = "admin";
        incorrectLoginForm.password = "niet-welkom-1337";

        RestAuthenticationFilter.LoginForm correctLoginForm = new RestAuthenticationFilter.LoginForm();
        correctLoginForm.username = "admin";
        correctLoginForm.password = "welkom";

        // The first two attempts are 'free'.
        webClient
                .perform(MockMvcRequestBuilders.post("/authentication")
                        .content(objectMapper.writeValueAsString(incorrectLoginForm)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());

        webClient
                .perform(MockMvcRequestBuilders.post("/authentication")
                        .content(objectMapper.writeValueAsString(incorrectLoginForm)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());

        // At the third incorrect attempt we tried too many times and we are blocked, for the 'Jan' username,
        // but not for the 'Piet' account.
        webClient
                .perform(MockMvcRequestBuilders.post("/authentication")
                        .content(objectMapper.writeValueAsString(incorrectLoginForm)))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(jsonPath("errorCode").value("TOO_MANY_LOGIN_ATTEMPTS"));

        loginAttemptService.resetByUsername(incorrectLoginForm.username);

        // The first two attempts are 'free'.
        webClient
                .perform(MockMvcRequestBuilders.post("/authentication")
                        .content(objectMapper.writeValueAsString(correctLoginForm)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
