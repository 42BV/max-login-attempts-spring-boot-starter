package nl._42.max_login_attempts_spring_boot_starter.filter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.io.IOUtils;

/**
 * Used to allow re-reads of login form in subsequent filters.
 * Source: https://stackoverflow.com/questions/4449096/how-to-read-request-getinputstream-multiple-times
 */
class ConsumingHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private byte[] body;

    ConsumingHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);

        try {
            body = IOUtils.toByteArray(request.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ServletInputStream getInputStream() {
        return new DelegatingServletInputStream(new ByteArrayInputStream(body));
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }
}
