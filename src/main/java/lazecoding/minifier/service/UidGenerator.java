package lazecoding.minifier.service;

import lazecoding.minifier.constant.CacheConstant;
import lazecoding.minifier.constant.SegmentConstant;
import lazecoding.minifier.util.BeanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 唯一 Id 生成
 *
 * @author lazecoding
 */
public class UidGenerator {

    private static final Logger logger = LoggerFactory.getLogger(UidGenerator.class);

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
        return getSegmentUid();
    }

    /**
     * 获取 SegmentUid
     *
     * @return
     */
    private static long getSegmentUid() {
        long uid = 0L;
        try {
            uid = BufferHolder.getId(SegmentConstant.SHORT_ID_TAG);
        } catch (Exception e) {
            logger.error("BufferHolder.getId for UidGenerator Exception", e);
            throw new RuntimeException(e);
        }
        return uid;

    }

    /**
     * 本地
     *
     * @return
     */
    private static long getLocalUid() {
        return localUid.incrementAndGet();
    }

    /**
     * Redis 生成
     */
    private static long getRedisUid() {
        RedisTemplate redisTemplate = BeanUtil.getBean("redisTemplate", RedisTemplate.class);
        Long num = redisTemplate.opsForValue().increment(CacheConstant.REDIS_UID.getName());
        return num == null ? 0L : num;
    }
}
