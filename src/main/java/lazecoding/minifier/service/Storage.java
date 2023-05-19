package lazecoding.minifier.service;

import com.github.benmanes.caffeine.cache.Cache;
import lazecoding.minifier.constant.CacheConstant;
import lazecoding.minifier.constant.CharConstant;
import lazecoding.minifier.constant.TableConstant;
import lazecoding.minifier.exception.NilParamException;
import lazecoding.minifier.mapper.UrlMapMapper;
import lazecoding.minifier.model.UrlMapBean;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

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

    /**
     * 存储转换信息
     *
     * @param conversionCode 短码
     * @param fullUrl        全地址
     */
    public void storageTransformInfo(String conversionCode, String fullUrl, long ttl) {
        // cachekey[minifier:transform:entity:conversionCode],cacheValue[transformCache]
        String cacheKey = CacheConstant.TRANSFORM_HEAD.getName() + conversionCode;
        // DB > Redis > Local
        // 1.DB
        String tableName = TableConstant.getUrlMapTable(conversionCode);
        urlMapMapper.addUrlMap(conversionCode, fullUrl, ttl, tableName);
        UrlMapBean urlMapBean = new UrlMapBean(conversionCode, fullUrl, ttl);
        urlMapBean.setCreateTime(Date.from(LocalDateTime.now().toInstant(ZoneOffset.of(CharConstant.ZONE_OFFSET))));
        // 2.Redis
        redisTemplate.opsForValue().set(cacheKey, urlMapBean, CacheConstant.TRANSFORM_HEAD.getTtl(), CacheConstant.TRANSFORM_HEAD.getTimeUnit());
        // 3.Local
        caffeineCache.put(cacheKey, urlMapBean);
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
        String fullUrl = "";
        String cacheKey = CacheConstant.TRANSFORM_HEAD.getName() + conversionCode;
        // 本地缓存
        UrlMapBean urlMapBean = (UrlMapBean) caffeineCache.getIfPresent(cacheKey);
        // 本地缓存不存在
        if (ObjectUtils.isEmpty(urlMapBean)) {
            // 查询分布式缓存
            urlMapBean = (UrlMapBean) redisTemplate.opsForValue().get(cacheKey);
            if (ObjectUtils.isEmpty(urlMapBean)) {
                //  Find From DB
                String tableName = TableConstant.getUrlMapTable(conversionCode);
                urlMapBean = urlMapMapper.findUrlMap(conversionCode, tableName);
                if (ObjectUtils.isEmpty(urlMapBean)) {
                    // null 也要设置缓存
                    urlMapBean = new UrlMapBean();
                }
                // 设置分布式缓存
                redisTemplate.opsForValue().set(cacheKey, urlMapBean, CacheConstant.TRANSFORM_HEAD.getTtl(), CacheConstant.TRANSFORM_HEAD.getTimeUnit());
            }
            // Local Must Do.
            caffeineCache.put(cacheKey, urlMapBean);
        }

        // transformCache 不可能为空了
        if (!ObjectUtils.isEmpty(urlMapBean) && LocalDateTime.now().toInstant(ZoneOffset.of(CharConstant.ZONE_OFFSET)).toEpochMilli() < urlMapBean.getTtl()) {
            fullUrl = urlMapBean.getFullUrl();
        }
        return fullUrl;
    }


}
