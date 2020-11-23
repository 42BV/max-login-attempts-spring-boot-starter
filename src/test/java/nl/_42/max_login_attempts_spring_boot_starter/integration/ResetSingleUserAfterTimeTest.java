package nl._42.max_login_attempts_spring_boot_starter.integration;

import java.time.LocalDateTime;

import nl._42.max_login_attempts_spring_boot_starter.AbstractWebIntegrationTest;
import nl._42.max_login_attempts_spring_boot_starter.AdjustableClock;
import nl._42.restsecure.autoconfigure.RestAuthenticationFilter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ActiveProfiles({ "clear-attempts", "disable-scheduling" })
class ResetSingleUserAfterTimeTest extends AbstractWebIntegrationTest {

    @Autowired
    private AdjustableClock adjustableClock;

    @Test
    void resetByUserAfterTime() throws Exception {
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

        adjustableClock.setTime(LocalDateTime.now().plusMinutes(35));

        webClient
                .perform(MockMvcRequestBuilders.post("/authentication")
                        .content(objectMapper.writeValueAsString(incorrectLoginForm)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());

        webClient
                .perform(MockMvcRequestBuilders.post("/authentication")
                        .content(objectMapper.writeValueAsString(incorrectLoginForm)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());

        webClient
                .perform(MockMvcRequestBuilders.post("/authentication")
                        .content(objectMapper.writeValueAsString(correctLoginForm)))
                .andExpect(MockMvcResultMatchers.status().isOk());

    }
}