package lazecoding.minifier.constant;

import java.util.concurrent.TimeUnit;

/**
 * 缓存
 *
 * @author lazecoding
 */
public enum CacheConstant {

    /**
     * 短地址和长地址映射缓存前缀
     */
    TRANSFORM_HEAD("minifier:transform:entity:", "短地址和长地址映射缓存名前缀", 60L * 60 * 12, TimeUnit.SECONDS),

    /**
     * 基于 Redis 的唯一 Id
     */
    REDIS_UID("minifier:uid", "基于 Redis 的唯一 Id", -1L, TimeUnit.SECONDS);

    private String name;

    private String desc;

    private Long ttl;

    private TimeUnit timeUnit;

    CacheConstant(String name, String desc, Long ttl, TimeUnit timeUnit) {
        this.name = name;
        this.desc = desc;
        this.ttl = ttl;
        this.timeUnit = timeUnit;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public Long getTtl() {
        return ttl;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }
}
