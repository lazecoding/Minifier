package lazecoding.minifier.controller;

import lazecoding.minifier.constant.DigitConstant;
import lazecoding.minifier.exception.IllegalUrlException;
import lazecoding.minifier.exception.NilParamException;
import lazecoding.minifier.model.BatchRequest;
import lazecoding.minifier.model.ResultBean;
import lazecoding.minifier.model.TransformBean;
import lazecoding.minifier.service.Transform;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.Flux;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * 全地址转换短地址
 *
 * @author lazecoding
 */
@Controller
@RequestMapping("transform")
public class TransformController {

    private static final Logger logger = LoggerFactory.getLogger(TransformController.class);

    @Autowired
    private Transform transform;

    /**
     * 全地址转换短地址
     */
    @PostMapping(path = "get")
    @ResponseBody
    public ResultBean transform(String path, Long timeout) {
        ResultBean resultBean = ResultBean.getInstance();
        if (timeout == null || timeout == 0L) {
            // 如果 timeout == null，默认超时时长 1 个月
            timeout = DigitConstant.TIMESTAMP_ONE_MONTH;
        }
        boolean isSuccess = false;
        String message = "";
        try {
            TransformBean transformBean = transform.getTransformedUrl(path, timeout);
            resultBean.setValue(transformBean);
            isSuccess = true;
        } catch (NilParamException e) {
            isSuccess = false;
            message = e.getMessage();
            logger.error(message, e);
        } catch (IllegalUrlException e) {
            isSuccess = false;
            message = "系统异常";
            logger.error(message, e);
        } catch (Exception e) {
            isSuccess = false;
            message = "系统异常";
            logger.error(message, e);
        }
        resultBean.setSuccess(isSuccess);
        resultBean.setMessage(message);
        return resultBean;
    }

    /**
     * 全地址转换短地址 批量
     */
    @PostMapping(path = "batch")
    @ResponseBody
    public ResultBean batch(@RequestBody BatchRequest batchRequest) {
        if (ObjectUtils.isEmpty(batchRequest)) {
            throw new NilParamException("BatchRequest Is Nil.");
        }
        Long timeout = batchRequest.getTimeout();
        List<String> list = batchRequest.getList();
        if (timeout == null || timeout == 0L) {
            // 如果 timeout == null，默认超时时长 1 个月
            timeout = DigitConstant.TIMESTAMP_ONE_MONTH;
        }
        ResultBean resultBean = ResultBean.getInstance();
        boolean isSuccess = false;
        String message = "";
        try {
            list = list.stream().filter(StringUtils::isNotBlank).collect(toList());
            List<TransformBean> transformBeanList = transform.batchTransformedUrl(list, timeout);
            resultBean.setValue(transformBeanList);
            isSuccess = true;
        } catch (NilParamException e) {
            isSuccess = false;
            message = e.getMessage();
            logger.error(message, e);
        } catch (IllegalUrlException e) {
            isSuccess = false;
            message = "系统异常";
            logger.error(message, e);
        } catch (Exception e) {
            isSuccess = false;
            message = "系统异常";
            logger.error(message, e);
        }
        resultBean.setSuccess(isSuccess);
        resultBean.setMessage(message);
        return resultBean;
    }

    /**
     * 全地址转换短地址 流
     */
    @PostMapping(path = "batch-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseBody
    public Flux<TransformBean> batchPush(@RequestBody BatchRequest batchRequest) {
        if (ObjectUtils.isEmpty(batchRequest)){
            throw new NilParamException("BatchRequest Is Nil.");
        }
        Long timeout = batchRequest.getTimeout();
        List<String> list = batchRequest.getList();
        if (timeout == null || timeout == 0L) {
            // 如果 timeout == null，默认超时时长 1 个月
            timeout = DigitConstant.TIMESTAMP_ONE_MONTH;
        }
        Long finalTimeout = timeout;
        return Flux.fromStream(list.stream()
                .filter(StringUtils::isNotBlank)
                .map((fullUrl) -> transform.getTransformedUrl(fullUrl, finalTimeout)));
    }

}
