package lazecoding.minifier.model;

import java.io.Serializable;

/**
 * 缓存实体
 *
 * @author lazecoding
 */
public class CacheBean<T> implements Serializable {

    private static final long serialVersionUID = 1L;

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
