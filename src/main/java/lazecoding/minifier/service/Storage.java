package lazecoding.minifier.service;

import com.github.benmanes.caffeine.cache.Cache;
import lazecoding.minifier.constant.CacheConstant;
import lazecoding.minifier.constant.CharConstant;
import lazecoding.minifier.exception.NilParamException;
import lazecoding.minifier.mapper.UrlMapMapper;
import lazecoding.minifier.model.CacheBean;
import lazecoding.minifier.model.UrlMapBean;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.TimeUnit;

/**
 * 存储模块
 *
 * @author lazecoding
 */
@Service
public class Storage {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private Cache<String, Object> caffeineCache;

    @Autowired
    private UrlMapMapper urlMapMapper;

    private static final String TABLE_HEAD = "url_map";

    /**
     * 根据 conversionCode 最后一位字符获取表名
     *
     * @param conversionCode 短码
     * @return tableName 表名
     */
    private String getTableName(String conversionCode) {
        if (StringUtils.isBlank(conversionCode)) {
            throw new NilParamException("conversionCode 不得为空");
        }
        String tableEnd = conversionCode.substring(conversionCode.length() - 1).toLowerCase();
        String tableName = TABLE_HEAD + "_" + tableEnd;
        return tableName;
    }

    /**
     * 存储转换信息
     *
     * @param conversionCode 短码
     * @param fullUrl        全地址
     */
    public void storageTransformInfo(String conversionCode, String fullUrl, long ttl) {
        // cachekey[minifier:transform:link:conversionCode],cacheValue[transformCache]
        String cacheKey = CacheConstant.TRANSFORM_HEAD.getName() + conversionCode;
        CacheBean<String> transformCache = new CacheBean<>(fullUrl, ttl);
        // DB > Redis > Local
        // 1.DB
        String tableName = this.getTableName(conversionCode);
        urlMapMapper.addUrlMap(conversionCode, fullUrl, ttl, tableName);
        // 2.Redis
        redisTemplate.opsForValue().set(cacheKey, transformCache, 60L, TimeUnit.MINUTES);
        // 3.Local
        caffeineCache.put(cacheKey, transformCache);
    }

    /**
     * 获取 fullUrl
     *
     * @param conversionCode 短码
     */
    public String getFullUrl(String conversionCode) {
        if (StringUtils.isBlank(conversionCode)) {
            throw new NilParamException("Code 不得为空");
        }
        String fullUrl;
        String cacheKey = CacheConstant.TRANSFORM_HEAD.getName() + conversionCode;
        // 本地缓存
        CacheBean<String> transformCache = (CacheBean<String>) caffeineCache.getIfPresent(cacheKey);
        // 如果缓存不存在 或者 超出了 ttl
        if (transformCache == null || LocalDateTime.now().toInstant(ZoneOffset.of(CharConstant.ZONE_OFFSET)).toEpochMilli() > transformCache.ttl) {
            // 分布式缓存
            transformCache = (CacheBean<String>) redisTemplate.opsForValue().get(cacheKey);
            if (ObjectUtils.isEmpty(transformCache)) {
                fullUrl = "";
                //  Find From DB
                String tableName = this.getTableName(conversionCode);
                UrlMapBean urlMap = urlMapMapper.findUrlMap(conversionCode, tableName);
                if (urlMap == null || LocalDateTime.now().toInstant(ZoneOffset.of(CharConstant.ZONE_OFFSET)).toEpochMilli() > urlMap.getTtl()) {
                    // null 缓存 5 分钟
                    transformCache = new CacheBean<>(fullUrl, LocalDateTime.now().plusMinutes(5).toInstant(ZoneOffset.of(CharConstant.ZONE_OFFSET)).toEpochMilli());
                } else {
                    transformCache = new CacheBean<>(urlMap.getFullUrl(), urlMap.getTtl());
                    // Redis
                    redisTemplate.opsForValue().set(cacheKey, transformCache, 60L, TimeUnit.MINUTES);
                }
            } else {
                fullUrl = transformCache.t;
            }
            // Local Must Do.
            caffeineCache.put(cacheKey, transformCache);
        } else {
            fullUrl = transformCache.t;
        }
        return fullUrl;
    }

}
