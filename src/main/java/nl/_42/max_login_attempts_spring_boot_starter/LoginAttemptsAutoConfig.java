package nl._42.max_login_attempts_spring_boot_starter;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "nl._42.max_login_attempts_spring_boot_starter")
@EnableConfigurationProperties
public class LoginAttemptsAutoConfig {

}
