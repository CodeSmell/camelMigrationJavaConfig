package codesmell.util;

public class RetryableException extends RuntimeException {
    public RetryableException() {
        super();
    }

    public RetryableException(String msg) {
        super(msg);
    }

    public RetryableException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public RetryableException(Throwable cause) {
        super(cause);
    }
}
