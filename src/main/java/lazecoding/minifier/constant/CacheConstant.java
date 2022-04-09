package lazecoding.minifier.constant;

/**
 * 缓存
 *
 * @author lazecoding
 */
public enum CacheConstant {

    /**
     * 短地址和长地址映射缓存前缀
     */
    TRANSFORM_HEAD("minifier:transform:link:", "短地址和长地址映射缓存名前缀"),

    /**
     * 基于 Redis 的唯一 Id
     */
    REDIS_UID("minifier:transform:uid", "基于 Redis 的唯一 Id");

    private String name;

    private String desc;

    CacheConstant(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }
}
