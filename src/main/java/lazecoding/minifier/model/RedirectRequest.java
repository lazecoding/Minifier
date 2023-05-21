package lazecoding.minifier.model;

import lazecoding.minifier.constant.CharConstant;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Map;

/**
 * 重定向请求
 *
 * @author lazecoding
 */
public class RedirectRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 请求头
     */
    private Map<String, String> headers;


    /**
     * cookies
     */
    private Map<String, String> cookies;

    /**
     * HTTP method
     */
    private String method;

    /**
     * 请求方 地址
     */
    private String remoteAddress;

    /**
     * 请求时间
     */
    private final Date date = Date.from(LocalDateTime.now().toInstant(ZoneOffset.of(CharConstant.ZONE_OFFSET)));

    public RedirectRequest() {
    }

    public RedirectRequest(Map<String, String> headers) {
        this.headers = headers;
    }


    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

    public void setCookies(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "RedirectRequest{" +
                "headers=" + headers +
                ", cookies=" + cookies +
                ", method='" + method + '\'' +
                ", remoteAddress='" + remoteAddress + '\'' +
                ", date=" + date +
                '}';
    }
}
