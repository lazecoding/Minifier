package lazecoding.minifier.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Caffeine Config
 *
 * @author lazecoding
 */
@Configuration
public class CaffeineConfig {

    private static final Logger logger = LoggerFactory.getLogger(CaffeineConfig.class);

    /**
     * 初始容量
     */
    @Value("${project.caffeine.initialCapacity}")
    private String initialCapacity;

    /**
     * 最大容量
     */
    @Value("${project.caffeine.maximumSize}")
    private String maximumSize;

    /**
     * 上次读写超过一定时间后过期，单位/s
     */
    @Value("${project.caffeine.expireAfterAccessNanos}")
    private String expireAfterAccessNanos;

    @Bean
    public Cache<String, Object> caffeineCache() {
        int initialCapacityValue = Math.max(Integer.parseInt(initialCapacity), 1024);
        int maximumSizeValue = Math.max(Integer.parseInt(maximumSize), 1024 * 10);
        int expireAfterAccessNanosValue = Math.max(Integer.parseInt(expireAfterAccessNanos), 60 * 60);
        logger.info("CaffeineConfig Init initialCapacity:[{}],maximumSize:[{}],expireAfterAccess:[{}]", initialCapacityValue, maximumSizeValue, expireAfterAccessNanosValue);
        return Caffeine.newBuilder()
                // 初始容量
                .initialCapacity(initialCapacityValue)
                // 最大容量
                .maximumSize(maximumSizeValue)
                // 上次读写超过一定时间后过期
                .expireAfterAccess(expireAfterAccessNanosValue, TimeUnit.SECONDS)
                .build();
    }
}
