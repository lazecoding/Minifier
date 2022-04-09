package lazecoding.minifier.model;

/**
 * 缓存实体
 *
 * @author lazecoding
 */
public class CacheBean<T> {

    /**
     * 缓存对象
     */
    public T t;

    /**
     * 生命周期
     */
    public long ttl;

    public CacheBean() {
    }

    public CacheBean(T t) {
        this.t = t;
        this.ttl = Long.MAX_VALUE;
    }

    public CacheBean(T t, long ttl) {
        this.t = t;
        this.ttl = ttl;
    }
}
