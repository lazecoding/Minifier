package lazecoding.minifier.controller;

import cn.hutool.http.useragent.Platform;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lazecoding.minifier.constant.CharConstant;
import lazecoding.minifier.model.RedirectRequest;
import lazecoding.minifier.service.Transform;
import lazecoding.minifier.util.IpUtil;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriUtils;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 重定向控制器
 * <p>
 * 为短地址重定向到全地址
 *
 * @author lazecoding
 */
@RestController
@CrossOrigin
@RequestMapping("s")
public class RedirectController {

    @Autowired
    private Transform transform;

    private static final ObjectMapper MAPPER = new ObjectMapper();


    /**
     * 为短地址重定向到全地址
     */
    @GetMapping(path = "/{code}")
    public void redirect(@PathVariable(name = "code") String code, ServerHttpResponse response, ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        InetSocketAddress remoteAddress = request.getRemoteAddress();
        remoteAddress.getHostName();
        remoteAddress.getHostString();
        System.out.println("====================================== start");

        System.out.println("IP " + remoteAddress.getAddress() + "  " + remoteAddress.getHostName() + "  " + remoteAddress.getHostString());

        InetAddress inetAddress = remoteAddress.getAddress();
        InetAddressValidator inetAddressValidator = InetAddressValidator.getInstance();
        System.out.println("inetAddress " + inetAddress.getAddress() + "  " + inetAddress.getHostName() + "  " + inetAddress.getHostAddress()+ "  " + inetAddress.getCanonicalHostName());
        System.out.println("IPV " + " 4?" + inetAddressValidator.isValidInet4Address(inetAddress.getHostAddress()) + " 6?" + inetAddressValidator.isValidInet6Address(inetAddress.getHostAddress()));



        String method = request.getMethodValue();

        // 请求头
        Map<String,String> headers = new HashMap<>();
        Map<String,String> cookies = new HashMap<>();
        HttpHeaders httpHeaders = request.getHeaders();
        Set<String> headerNames = httpHeaders.keySet();

        UserAgent userAgent = null;
        if (!CollectionUtils.isEmpty(headerNames)) {
            // 遍历 headers
            headerNames.forEach(headerName -> {
                String headerValue = httpHeaders.getFirst(headerName);
                response.getHeaders().set(headerName, headerValue);
                headers.put(headerName, headerValue);
            });
            // 解析 cookie
            String cookieStr = httpHeaders.getFirst("Cookie");
            if (StringUtils.hasText(cookieStr)) {
                String[] cookieSplit = cookieStr.split(";");
                if (!ObjectUtils.isEmpty(cookieSplit) && cookieSplit.length > 0) {
                    for (String cookieItem : cookieSplit) {
                        cookieItem = cookieItem.trim();
                        String[] singleCookie = cookieItem.split("=");
                        if (!ObjectUtils.isEmpty(singleCookie) && singleCookie.length == 2) {
                            String cookieKey = singleCookie[0];
                            String cookieValue = singleCookie[1];
                            cookies.put(cookieKey, cookieValue);
                        }
                    }
                }
            }
            // 解析用户标识
            String userAgentStr = httpHeaders.getFirst("User-Agent");
            if (StringUtils.hasText(userAgentStr)) {
                userAgentStr = httpHeaders.getFirst("user-agent");
            }
            userAgent = UserAgentUtil.parse(userAgentStr);
        }
        String ip = IpUtil.getRequestIp(httpHeaders, inetAddress.getHostAddress());

        System.out.println("userAgent:" + userAgent + "    " + ip);
        if (!ObjectUtils.isEmpty(userAgent)) {
            // 设备类型
            Platform platform = userAgent.getPlatform();
            String clientType = platform.getName();
            // 操作系统类型
            String os = userAgent.getOs().getName();
            // String ip = IpUti.getIpAddr(request);
            // 浏览器类型
            String browser = userAgent.getBrowser().getName();


            platform.isUnknown();
            platform.isAndroid();
            platform.isIos();
            platform.isIPad();
            platform.isIPhoneOrIPod();


            System.out.println("设备类型：" + clientType + " 操作系统 OS:" + os + " 浏览器类型:" + browser + " engine:" + userAgent.getEngine().getName());

            System.out.println("设备类型：" + clientType + " isUnknown:" + platform.isUnknown() + " isAndroid:" + platform.isAndroid() + " isIos:" + platform.isIos()
                    + " isIPad:" + platform.isIPad() + " isIPhoneOrIPod:" + platform.isIPhoneOrIPod());

        }

        RedirectRequest redirectRequest = new RedirectRequest();
        redirectRequest.setHeaders(headers);
        redirectRequest.setCookies(cookies);
        redirectRequest.setMethod(method);
        redirectRequest.setRemoteAddress(ObjectUtils.isEmpty(remoteAddress) ? "" : remoteAddress.toString());
        String redirectRequestJson = "";

        try {
            redirectRequestJson = MAPPER.writeValueAsString(redirectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(redirectRequestJson);
        System.out.println("====================================== end");


        // 跨域处理
        response.getHeaders().set(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, CorsConfiguration.ALL);
        response.getHeaders().set(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, CorsConfiguration.ALL);
        response.getHeaders().set(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, Boolean.TRUE.toString());

        // 获取真实地址
        String location = transform.getFullUrl(code);
        // URL Encode
        location = UriUtils.encodeFragment(location, CharConstant.URL_ENCODE_CHARSET);
        if (!StringUtils.hasText(location)) {
            // 404
            response.setStatusCode(HttpStatus.NOT_FOUND);
            return;
        }
        // 重定向
        // 307/308 与 302/301 的行为并行，但不允许 HTTP 方法改变。 307:TEMPORARY_REDIRECT ; 308:PERMANENT_REDIRECT
        response.setStatusCode(HttpStatus.TEMPORARY_REDIRECT);

        // 设置重定向地址
        response.getHeaders().setLocation(URI.create(location));
    }

}
