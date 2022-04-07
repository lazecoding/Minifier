package lazecoding.minifier.service;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 唯一 Id 生成
 *
 * @author liux
 */
public class UidGenerator {

    /**
     * 本地 Uid （demo）
     */
    private static AtomicLong localUid = new AtomicLong(0);

    /**
     * 获取 Uid
     *
     * @return
     */
    public static long getUid() {
        // TODO 获取分布式 Id
        return localUid.incrementAndGet();
    }

}
