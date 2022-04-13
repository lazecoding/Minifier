package lazecoding.minifier.service;

import lazecoding.minifier.config.ServerConfig;
import lazecoding.minifier.constant.CharConstant;
import lazecoding.minifier.exception.NilParamException;
import lazecoding.minifier.model.TransformBean;
import lazecoding.minifier.util.ConversionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

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
    private ServerConfig serverConfig;

    @Autowired
    private Storage storage;

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
        // 4.存储
        storage.storageTransformInfo(conversionCode, fullUrl);
        return shortUri;
    }

    /**
     * 批量获取(转换)短地址
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
     * 获取(转换)短地址
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
        return storage.getFullUrl(conversionCode);
    }

}
