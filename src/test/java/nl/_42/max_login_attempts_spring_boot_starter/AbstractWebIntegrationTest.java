package nl._42.max_login_attempts_spring_boot_starter;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import tools.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

public abstract class AbstractWebIntegrationTest extends AbstractSpringTest {

    @Autowired
    protected WebApplicationContext applicationContext;
    @Autowired
    protected ObjectMapper objectMapper;

    protected MockMvc webClient;

    @BeforeEach
    public void setUpMockMvc() {
        webClient = webAppContextSetup(applicationContext)
            .apply(springSecurity())
            .defaultRequest(get("/")
                .contentType(APPLICATION_JSON)
                .with(anonymous()))
            .alwaysDo(log())
            .build();
    }

}
