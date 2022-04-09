package lazecoding.minifier.service;

import lazecoding.minifier.constant.CacheConstant;
import lazecoding.minifier.util.BeanUtil;
import org.springframework.data.redis.core.RedisTemplate;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 唯一 Id 生成
 *
 * @author lazecoding
 */
public class UidGenerator {

    /**
     * 本地 Uid
     */
    private static AtomicLong localUid = new AtomicLong(0);

    /**
     * 获取 Uid
     *
     * @return
     */
    public static long getUid() {
        // TODO 获取分布式 Id
        return getRedisUid();
    }

    /**
     * 本地
     * @return
     */
    public static long getLocalUid() {
        return localUid.incrementAndGet();
    }

    /**
     * Redis 生成
     */
    public static long getRedisUid() {
        RedisTemplate redisTemplate = BeanUtil.getBean("redisTemplate", RedisTemplate.class);
        Long num = redisTemplate.opsForValue().increment(CacheConstant.REDIS_UID.getName());
        return num == null ? 0L : num;
    }
}
