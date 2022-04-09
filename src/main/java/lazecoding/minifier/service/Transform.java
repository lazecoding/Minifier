package lazecoding.minifier.service;

import com.github.benmanes.caffeine.cache.Cache;
import lazecoding.minifier.config.ServerConfig;
import lazecoding.minifier.constant.CacheConstant;
import lazecoding.minifier.constant.CharConstant;
import lazecoding.minifier.exception.NilParamException;
import lazecoding.minifier.model.CacheBean;
import lazecoding.minifier.model.TransformBean;
import lazecoding.minifier.util.ConversionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

/**
 * 转换
 *
 * @author lazecoding
 */
@Component
public class Transform {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * Caffeine Cache
     */
    @Autowired
    Cache<String, Object> caffeineCache;

    @Autowired
    private ServerConfig serverConfig;

    /**
     * 地址转换
     *
     * @param fullUrl 原地址
     * @return shortUri 转换后的 uri
     */
    private String transformUrl(String fullUrl) {
        if (StringUtils.isBlank(fullUrl)) {
            throw new NilParamException("原地址不得为空");
        }
        // 1.申请 id
        long uid = UidGenerator.getUid();
        // 2.进制转换，获取短码
        String conversionCode = ConversionUtils.X.encode62(uid);
        // 3.构建短地址
        String shortUri = CharConstant.TRANSFORM_ROUTE + conversionCode;
        // 4.持久化
        // cachekey[minifier:transform:link:conversionCode],cacheValue[transformCache]
        String cacheKey = CacheConstant.TRANSFORM_HEAD.getName() + conversionCode;
        CacheBean<String> transformCache = new CacheBean<>(fullUrl);
        // DB > Redis > Local
        // TODO DB
        redisTemplate.opsForValue().set(cacheKey, transformCache);
        caffeineCache.put(cacheKey, transformCache);
        return shortUri;
    }

    /**
     * 批量获取短地址
     **/
    public List<TransformBean> batchTransformedUrl(List<String> list) {
        if (CollectionUtils.isEmpty(list)) {
            throw new NilParamException("Batch List 不得为空");
        }
        List<TransformBean> transformBeanList = new ArrayList<>();
        for (String fullUrl : list) {
            TransformBean transformBean = this.getTransformedUrl(fullUrl);
            transformBeanList.add(transformBean);
        }
        return transformBeanList;
    }

    /**
     * 获取短地址
     *
     * @param fullUrl 原地址
     * @return shortUrl 短地址
     */
    public TransformBean getTransformedUrl(String fullUrl) {
        String domain = serverConfig.getDomain();
        if (StringUtils.isBlank(domain)) {
            throw new NilParamException("Domain 不得为空");
        }
        String shortUrl = domain + transformUrl(fullUrl);
        return new TransformBean(fullUrl, shortUrl);
    }

    /**
     * 根据编码获取全地址
     *
     * @param conversionCode base62 code
     * @return
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
                // null 缓存 5 分钟
                transformCache = new CacheBean<>(fullUrl, LocalDateTime.now().plusSeconds(5).toInstant(ZoneOffset.of(CharConstant.ZONE_OFFSET)).toEpochMilli());
            } else {
                fullUrl = transformCache.t;
            }
            caffeineCache.put(cacheKey, transformCache);
        } else {
            fullUrl = transformCache.t;
        }
        return fullUrl;
    }

}
