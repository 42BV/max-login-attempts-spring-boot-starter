package nl._42.max_login_attempts_spring_boot_starter;

import nl._42.max_login_attempts_spring_boot_starter.error.TooManyLoginAttemptsErrorHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
@ComponentScan(basePackageClasses = LoginAttemptsAutoConfig.class)
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
