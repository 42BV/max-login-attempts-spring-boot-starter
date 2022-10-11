package nl._42.max_login_attempts_spring_boot_starter.listener;

import javax.servlet.http.HttpServletRequest;

import nl._42.max_login_attempts_spring_boot_starter.service.LoginAttemptService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Listener which retrieves the remote address of the user when a login attempt fails and notifies
 * the LoginAttemptService of this fact.
 */
@Component
public class AuthenticationFailureListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

    private final LoginAttemptService loginAttemptService;

    @Autowired
    public AuthenticationFailureListener(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }

    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes attributes) {
            String username = String.valueOf(event.getAuthentication().getPrincipal());
            HttpServletRequest request = attributes.getRequest();

            boolean nowBlocked = loginAttemptService.loginFailed(username, request.getRemoteAddr());
            if (nowBlocked) {
                /* If the user is now blocked we throw a UsernameRemoteAddressBlockedException, which is caught in
                 * the LoginAttemptFilter. Without doing this we will only get an error the next time the user
                 * attempts to log in, but the user is already blocked now.
                 */
                throw new UsernameRemoteAddressBlockedException(username, request.getRemoteAddr());
            }
        }
    }
}
