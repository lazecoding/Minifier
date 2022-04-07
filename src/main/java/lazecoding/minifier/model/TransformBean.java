package lazecoding.minifier.model;

/**
 * 转换映射 Bean
 *
 * @author liux
 */
public class TransformBean {
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
