package nl._42.max_login_attempts_spring_boot_starter;

import java.util.Collections;
import java.util.Set;

import nl._42.restsecure.autoconfigure.authentication.AbstractUserDetailsService;
import nl._42.restsecure.autoconfigure.authentication.RegisteredUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

public class MockUserDetailsService extends AbstractUserDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    protected RegisteredUser findUserByUsername(String user) {
        return new RegisteredUser() {

            @Override
            public String getUsername() {
                return user;
            }

            @Override
            public String getPassword() {
                return passwordEncoder.encode("welkom");
            }

            @Override
            public Set<String> getAuthorities() {
                return Collections.singleton("admin");
            }
        };
    }
}
