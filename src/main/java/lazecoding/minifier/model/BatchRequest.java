package lazecoding.minifier.model;

import java.util.List;

/**
 *
 * 批量请求 Bean
 *
 * @author lazecoding
 */
public class BatchRequest {

    /**
     * 待转换的原地址
     */
    List<String> list;

    /**
     * 超时时间
     */
    Long timeout;

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public Long getTimeout() {
        return timeout;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }

    @Override
    public String toString() {
        return "BatchRequest{" +
                "list=" + list +
                ", timeout=" + timeout +
                '}';
    }
}
