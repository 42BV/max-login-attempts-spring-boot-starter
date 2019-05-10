package nl._42.max_login_attempts_spring_boot_starter.error;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

/**
 * You can create a bean implementing this interface
 * if you wish to override the default result that is
 * sent back to the user when the login attempt limit
 * has been reached.
 */
@Component
public interface TooManyLoginAttemptsErrorHandler {

    /**
     * Handling method for when a user has attempted to login too many times.
     * Can be used to return a result on the HttpServletResponse.
     *
     * @param response HttpServletResponse
     * @throws IOException exception
     */
    void handle(HttpServletResponse response) throws IOException;
}