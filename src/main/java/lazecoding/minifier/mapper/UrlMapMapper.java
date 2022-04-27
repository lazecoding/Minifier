package lazecoding.minifier.mapper;

import lazecoding.minifier.model.UrlMapBean;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * UrlMapMapper
 *
 * @author lazecoding
 */
@Mapper
public interface UrlMapMapper {

    /**
     * 短码-全地址 映射 入库
     *
     * @param conversionCode 短码
     * @param fullUrl        全地址
     */
    void addUrlMap(@Param("conversionCode") String conversionCode, @Param("fullUrl") String fullUrl,@Param("ttl") long ttl);

    /**
     * 获取 短码-全地址 映射
     *
     * @param conversionCode 短码
     * @return UrlMapBean
     */
    UrlMapBean findUrlMap(@Param("conversionCode") String conversionCode);

}
