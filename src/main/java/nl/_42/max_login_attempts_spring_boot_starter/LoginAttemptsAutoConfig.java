package nl._42.max_login_attempts_spring_boot_starter;

import java.time.Clock;

import nl._42.max_login_attempts_spring_boot_starter.error.TooManyLoginAttemptsErrorHandler;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "nl._42.max_login_attempts_spring_boot_starter")
@EnableConfigurationProperties
public class LoginAttemptsAutoConfig {

    @Bean
    @ConditionalOnMissingBean(Clock.class)
    public Clock clock() {
        return Clock.systemDefaultZone();
    }

    @Bean
    @ConditionalOnMissingBean(TooManyLoginAttemptsErrorHandler.class)
    public TooManyLoginAttemptsErrorHandler tooManyLoginAttemptsErrorHandler() {
        return new DefaultTooManyLoginAttemptsErrorHandler();
    }
}
