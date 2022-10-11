package nl._42.max_login_attempts_spring_boot_starter;

import java.time.Clock;

import nl._42.max_login_attempts_spring_boot_starter.filter.LoginAttemptFilter;
import nl._42.restsecure.autoconfigure.HttpSecurityCustomizer;
import nl._42.restsecure.autoconfigure.RestAuthenticationFilter;
import nl._42.restsecure.autoconfigure.WebSecurityAutoConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@ComponentScan(value = "nl._42.max_login_attempts_spring_boot_starter")
@Import({ WebSecurityAutoConfig.class })
@EnableWebMvc
public class TestConfiguration {

    @Autowired
    @Lazy
    private LoginAttemptFilter loginAttemptFilter;

    @Bean
    public Clock clock() {
        return new AdjustableClock();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public MockUserDetailsService mockUserDetailsService() {
        return new MockUserDetailsService();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public HttpSecurityCustomizer httpSecurityCustomizer() {
        return http -> http.csrf().disable().addFilterBefore(loginAttemptFilter, RestAuthenticationFilter.class);
    }
}
