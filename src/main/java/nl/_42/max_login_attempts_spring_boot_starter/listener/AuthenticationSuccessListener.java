package nl._42.max_login_attempts_spring_boot_starter.listener;

import javax.servlet.http.HttpServletRequest;

import nl._42.max_login_attempts_spring_boot_starter.service.LoginAttemptService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
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
            String username = String.valueOf(event.getAuthentication().getPrincipal());
            HttpServletRequest request = attributes.getRequest();
            loginAttemptService.loginSucceeded(username, request.getRemoteAddr());
        }
    }
}
