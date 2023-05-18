package lazecoding.minifier.model;

import java.io.Serializable;

/**
 * 转换映射 Bean
 *
 * @author lazecoding
 */
public class TransformBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 全地址
     */
    private String fullUrl;

    /**
     * 短地址
     */
    private String shortUrl;

    public TransformBean(String fullUrl, String shortUrl) {
        this.fullUrl = fullUrl;
        this.shortUrl = shortUrl;
    }

    public String getFullUrl() {
        return fullUrl;
    }

    public void setFullUrl(String fullUrl) {
        this.fullUrl = fullUrl;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }
}
