package nl._42.max_login_attempts_spring_boot_starter;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.io.IOException;
import java.util.Collections;

import jakarta.servlet.http.HttpServletResponse;
import nl._42.max_login_attempts_spring_boot_starter.error.TooManyLoginAttemptsErrorHandler;
import tools.jackson.databind.json.JsonMapper;

/**
 * Default implementation of TooManyLoginAttemptsErrorHandler.
 * It outputs a JSON object with a field "errorCode" and a value
 * "TOO_MANY_LOGIN_ATTEMPTS". The status is 403 Forbidden.
 */
class DefaultTooManyLoginAttemptsErrorHandler implements TooManyLoginAttemptsErrorHandler {

    @Override
    public void handle(HttpServletResponse response) throws IOException {
        JsonMapper objectMapper = JsonMapper.builder().build();
        response.setStatus(FORBIDDEN.value());
        response.setContentType(APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), Collections.singletonMap("errorCode", "TOO_MANY_LOGIN_ATTEMPTS"));
        response.getWriter().flush();
        response.flushBuffer();
    }
}
