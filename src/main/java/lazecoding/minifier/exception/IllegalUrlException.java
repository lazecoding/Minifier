package lazecoding.minifier.exception;

/**
 * 不合法链接
 *
 * @author lazecoding
 */
public class IllegalUrlException extends RuntimeException {

    public IllegalUrlException() {
        super("地址不合法");
    }

    public IllegalUrlException(String msg) {
        super(msg);
    }
}
