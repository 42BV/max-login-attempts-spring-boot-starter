package nl._42.max_login_attempts_spring_boot_starter;

import java.time.Clock;

import nl._42.max_login_attempts_spring_boot_starter.filter.LoginAttemptFilter;
import nl._42.restsecure.autoconfigure.HttpSecurityCustomizer;
import nl._42.restsecure.autoconfigure.RestAuthenticationFilter;
import nl._42.restsecure.autoconfigure.WebSecurityAutoConfig;
import nl._42.restsecure.autoconfigure.errorhandling.GenericErrorHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@ComponentScan(value = "nl._42.max_login_attempts_spring_boot_starter")
@Import({ WebSecurityAutoConfig.class })
@EnableWebSecurity
@EnableWebMvc
@Order(200)
public class TestConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    @Lazy
    private LoginAttemptFilter loginAttemptFilter;

    @Autowired
    private GenericErrorHandler genericErrorHandler;

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
        return http -> http.csrf().disable().addFilter(new RestAuthenticationFilter(genericErrorHandler, authenticationManager())).addFilterBefore(loginAttemptFilter, RestAuthenticationFilter.class);
    }
}
