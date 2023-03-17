package lazecoding.minifier.listener;

import lazecoding.minifier.service.BufferHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * AppStartupRunner 系统启动后执行
 *
 * @author lazecoding
 */
@Component
public class AppStartupRunner implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(AppStartupRunner.class);


    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            // 初始化
            BufferHolder.init();
        } catch (Exception e) {
            logger.error("ApplicationRunner Exception", e);
        }
    }
}
