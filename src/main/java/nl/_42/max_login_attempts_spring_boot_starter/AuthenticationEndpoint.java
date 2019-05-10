package nl._42.max_login_attempts_spring_boot_starter;

import org.springframework.http.HttpMethod;

public class AuthenticationEndpoint {

    private String path;
    private HttpMethod method;

    public static AuthenticationEndpoint create(String path, HttpMethod method) {
        AuthenticationEndpoint authenticationEndpoint = new AuthenticationEndpoint();
        authenticationEndpoint.path = path;
        authenticationEndpoint.method = method;
        return authenticationEndpoint;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }
}
