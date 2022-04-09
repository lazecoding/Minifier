package lazecoding.minifier.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
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
    @Bean
    public Cache<String, Object> caffeineCache() {
        return Caffeine.newBuilder()
                // 初始容量
                .initialCapacity(128)
                // 最大容量
                .maximumSize(1024 * 10)
                // 上次读写超过一定时间后过期
                .expireAfterAccess(60, TimeUnit.MINUTES)
                .build();
    }
}
