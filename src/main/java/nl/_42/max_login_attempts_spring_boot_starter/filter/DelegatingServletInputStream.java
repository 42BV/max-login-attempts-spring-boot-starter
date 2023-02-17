package nl._42.max_login_attempts_spring_boot_starter.filter;

import java.io.IOException;
import java.io.InputStream;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;

/**
 * Used to create the {@link ConsumingHttpServletRequestWrapper}.
 * Source: https://stackoverflow.com/questions/4449096/how-to-read-request-getinputstream-multiple-times
 */
class DelegatingServletInputStream extends ServletInputStream {

    private final InputStream sourceStream;

    private boolean finished = false;

    /**
     * Create a DelegatingServletInputStream for the given source stream.
     *
     * @param sourceStream the source stream (never {@code null})
     */
    DelegatingServletInputStream(InputStream sourceStream) {
        this.sourceStream = sourceStream;
    }

    @Override
    public int read() throws IOException {
        int data = this.sourceStream.read();
        if (data == -1) {
            this.finished = true;
        }
        return data;
    }

    @Override
    public int available() throws IOException {
        return this.sourceStream.available();
    }

    @Override
    public void close() throws IOException {
        super.close();
        this.sourceStream.close();
    }

    @Override
    public boolean isFinished() {
        return this.finished;
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setReadListener(ReadListener readListener) {
        throw new UnsupportedOperationException();
    }
}
