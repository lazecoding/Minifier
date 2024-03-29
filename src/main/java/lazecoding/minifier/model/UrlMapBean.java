package lazecoding.minifier.model;

import java.io.Serializable;
import java.util.Date;

/**
 * 短码-全地址 映射
 *
 * @author lazecoding
 */
public class UrlMapBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 短码
     */
    private String conversionCode;

    /**
     * 全地址
     */
    private String fullUrl;

    /**
     * 存活时间
     */
    private long ttl;

    /**
     * 创建时间
     */
    private Date createTime;

    public UrlMapBean() {
    }

    public UrlMapBean(String conversionCode, String fullUrl, long ttl) {
        this.conversionCode = conversionCode;
        this.fullUrl = fullUrl;
        this.ttl = ttl;
    }

    public String getConversionCode() {
        return conversionCode;
    }

    public void setConversionCode(String conversionCode) {
        this.conversionCode = conversionCode;
    }

    public String getFullUrl() {
        return fullUrl;
    }

    public void setFullUrl(String fullUrl) {
        this.fullUrl = fullUrl;
    }

    public long getTtl() {
        return ttl;
    }

    public void setTtl(long ttl) {
        this.ttl = ttl;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
