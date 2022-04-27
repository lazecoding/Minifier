package lazecoding.minifier.controller;

import lazecoding.minifier.constant.DigitConstant;
import lazecoding.minifier.model.TransformBean;
import lazecoding.minifier.service.Transform;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

    @Autowired
    private Transform transform;

    /**
     * 全地址转换短地址
     */
    @PostMapping(path = "get")
    @ResponseBody
    public Mono<TransformBean> transform(String path, Long timeout) {
        if (timeout == null) {
            // 如果 timeout == null，默认超时时长 1 个月
            timeout = DigitConstant.TIMESTAMP_ONE_MONTH;
        }
        TransformBean transformBean = transform.getTransformedUrl(path, timeout);
        return Mono.just(transformBean);
    }

    /**
     * 全地址转换短地址 批量
     */
    @PostMapping(path = "batch")
    @ResponseBody
    public Flux<TransformBean> batch(@RequestBody List<String> list, Long timeout) {
        if (timeout == null) {
            // 如果 timeout == null，默认超时时长 1 个月
            timeout = DigitConstant.TIMESTAMP_ONE_MONTH;
        }
        list = list.stream().filter(StringUtils::isNotBlank).collect(toList());
        List<TransformBean> transformBeanList = transform.batchTransformedUrl(list, timeout);
        return Flux.fromIterable(transformBeanList);
    }

    /**
     * 全地址转换短地址 流
     */
    @PostMapping(path = "batch-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseBody
    public Flux<TransformBean> batchPush(@RequestBody List<String> list, Long timeout) {
        if (timeout == null) {
            // 如果 timeout == null，默认超时时长 1 个月
            timeout = DigitConstant.TIMESTAMP_ONE_MONTH;
        }
        Long finalTimeout = timeout;
        return Flux.fromStream(list.stream()
                .filter(StringUtils::isNotBlank)
                .map((fullUrl) -> transform.getTransformedUrl(fullUrl, finalTimeout)));
    }

}
