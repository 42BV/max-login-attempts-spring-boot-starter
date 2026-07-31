package nl._42.max_login_attempts_spring_boot_starter.listener;

import jakarta.servlet.http.HttpServletRequest;

import nl._42.max_login_attempts_spring_boot_starter.service.LoginAttemptService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class AuthenticationSuccessListener implements ApplicationListener<AuthenticationSuccessEvent> {

    private final LoginAttemptService loginAttemptService;

    @Autowired
    public AuthenticationSuccessListener(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes attributes) {
            String username = extractUsername(event.getAuthentication().getPrincipal());
            HttpServletRequest request = attributes.getRequest();
            loginAttemptService.loginSucceeded(username, request.getRemoteAddr());
        }
    }

    /**
     * On a successful authentication the principal is no longer the username typed in the login
     * form but the {@link UserDetails} of the authenticated user, whose toString is not a username.
     */
    private String extractUsername(Object principal) {
        if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        }
        return String.valueOf(principal);
    }
}
