package lazecoding.minifier.controller;

import lazecoding.minifier.constant.CharConstant;
import lazecoding.minifier.service.Transform;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriUtils;

import java.net.URI;
import java.util.Set;

/**
 * 重定向控制器
 * <p>
 * 为短地址重定向到全地址
 *
 * @author lazecoding
 */
@RestController
@RequestMapping("s")
public class RedirectController {

    @Autowired
    private Transform transform;

    /**
     * 为短地址重定向到全地址
     */
    @GetMapping(path = "/{code}")
    public void redirect(@PathVariable(name = "code") String code, ServerHttpResponse response, ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders httpHeaders = request.getHeaders();
        Set<String> headerNames = httpHeaders.keySet();
        if (!CollectionUtils.isEmpty(headerNames)) {
            headerNames.forEach(headerName -> {
                String headerValue = httpHeaders.getFirst(headerName);
                response.getHeaders().set(headerName, headerValue);
            });
        }

        // 跨域处理
        response.getHeaders().set(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, CorsConfiguration.ALL);
        response.getHeaders().set(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, CorsConfiguration.ALL);
        response.getHeaders().set(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, Boolean.TRUE.toString());

        // 获取真实地址
        String location = transform.getFullUrl(code);
        // URL Encode
        location = UriUtils.encodeFragment(location, CharConstant.URL_ENCODE_CHARSET);
        if (StringUtils.isBlank(location)) {
            // 永久重定向
            // 307/308 与 302/301 的行为并行，但不允许 HTTP 方法改变。
            response.setStatusCode(HttpStatus.NOT_FOUND);
            return;
        }
        // 永久重定向
        // 307/308 与 302/301 的行为并行，但不允许 HTTP 方法改变。 307:TEMPORARY_REDIRECT ; 308:PERMANENT_REDIRECT
        response.setStatusCode(HttpStatus.TEMPORARY_REDIRECT);

        // 设置重定向地址
        response.getHeaders().setLocation(URI.create(location));
    }

}
