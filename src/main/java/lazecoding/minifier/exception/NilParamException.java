package lazecoding.minifier.exception;

/**
 * 空参数异常
 *
 * @author lazecoding
 */
public class NilParamException extends RuntimeException {
    public NilParamException(String msg) {
        super(msg);
    }
}
