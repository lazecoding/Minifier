package lazecoding.minifier.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * WebSocket 服务器配置
 *
 * @author lazecoding
 */
@Configuration("serverConfig")
@ConfigurationProperties(prefix = "project.server-config")
public class ServerConfig {

    /**
     * 服务域名
     */
    private String domain = "";

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }
}

