package lazecoding.minifier.constant;

/**
 * 缓存
 *
 * @author liux
 */
public enum CacheConstant {

    /**
     * 短地址和长地址映射缓存前缀
     */
    TRANSFORM_HEAD("transform:", "短地址和长地址映射缓存名前缀");

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
