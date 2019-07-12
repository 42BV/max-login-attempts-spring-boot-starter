package nl._42.max_login_attempts_spring_boot_starter.filter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl._42.max_login_attempts_spring_boot_starter.LoginAttemptConfiguration;
import nl._42.max_login_attempts_spring_boot_starter.error.TooManyLoginAttemptsErrorHandler;
import nl._42.max_login_attempts_spring_boot_starter.listener.UsernameRemoteAddressBlockedException;
import nl._42.max_login_attempts_spring_boot_starter.service.LoginAttemptService;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Filter which checks if the login request is executed while the user is already blocked
 * due to an excessive amount of login attempts. If so, a forbidden status with a specific
 * error code is returned.
 */
@Component
public class LoginAttemptFilter extends OncePerRequestFilter {

    private final LoginAttemptService loginAttemptService;
    private final TooManyLoginAttemptsErrorHandler tooManyLoginAttemptsErrorHandler;

    private final LoginAttemptConfiguration loginAttemptConfiguration;

    private final List<AntPathRequestMatcher> authenticationRequestMatchers;

    @Autowired
    public LoginAttemptFilter(
        LoginAttemptService loginAttemptService,
        TooManyLoginAttemptsErrorHandler tooManyLoginAttemptsErrorHandler,
        LoginAttemptConfiguration loginAttemptConfiguration
    ) {
        this.loginAttemptService = loginAttemptService;
        this.tooManyLoginAttemptsErrorHandler = tooManyLoginAttemptsErrorHandler;
        this.loginAttemptConfiguration = loginAttemptConfiguration;

        this.authenticationRequestMatchers = loginAttemptConfiguration.getAuthenticationEndpoints()
                .stream()
                .map(loginEndpoint -> new AntPathRequestMatcher(loginEndpoint.getPath(), loginEndpoint.getMethod().name()))
                .collect(Collectors.toList());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        /*
          The request passed to the next filter is,
          by default, the normal incoming HttpServletRequest.
          If we attempt to check the username here we wrap it in
          a {@link ConsumingHttpServletRequestWrapper} so it can
          be reread in the {@link nl._42.restsecure.autoconfigure.RestAuthenticationFilter}.
         */
        HttpServletRequest passedRequest = request;

        if (loginAttemptConfiguration.isEnabled() && authenticationRequestMatchers.stream().anyMatch(matcher -> matcher.matches(request))) {

            /*
             * If this is the login request we wrap the HttpServletRequest
             * before reading it, because once the {@link HttpServletRequest}
             * is read it is exhausted.
             */
            passedRequest = new ConsumingHttpServletRequestWrapper(request);

            if (loginAttemptService.isBlocked(readUsername(passedRequest), request.getRemoteAddr())) {
                handleUsernameIpAddressBlocked(response);
                return;
            }
        }

        /*
         * If this request is the one that causes the block we need
         * to handle the error now, and not in the next request.
         */
        try {
            filterChain.doFilter(passedRequest, response);
        } catch (UsernameRemoteAddressBlockedException e) {
            handleUsernameIpAddressBlocked(response);
        }
    }

    private void handleUsernameIpAddressBlocked(HttpServletResponse response) throws IOException {
        tooManyLoginAttemptsErrorHandler.handle(response);
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    private String readUsername(HttpServletRequest request) throws IOException {
        String loginFormJson = IOUtils.toString(request.getReader());
        ObjectNode node = new ObjectMapper().readValue(loginFormJson, ObjectNode.class);
        return node.get("username").asText();
    }
}
