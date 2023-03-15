package lazecoding.minifier.service;

import lazecoding.minifier.constant.SegmentConstant;
import lazecoding.minifier.exception.InitException;
import lazecoding.minifier.exception.NilTagException;
import lazecoding.minifier.model.Record;
import lazecoding.minifier.model.Segment;
import lazecoding.minifier.model.SegmentBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * SegmentBuffer 持有者
 *
 * @author lazecoding
 * @apiNote init() 初始化; getId() 获取分布式 Id。
 */
public class BufferHolder {

    private static final Logger logger = LoggerFactory.getLogger(BufferHolder.class);

    /**
     * 线程池
     */
    private static final ExecutorService service = new ThreadPoolExecutor(2, 4, 60L, TimeUnit.SECONDS, new SynchronousQueue<>());

    /**
     * 标识初始化是否成功
     */
    private static volatile boolean initSuccess = false;

    /**
     * IdCache
     */
    private static final Map<String, SegmentBuffer> IdCache = new ConcurrentHashMap<>();

    /**
     * 初始化
     */
    public static boolean init() {
        String tag = SegmentConstant.SHORT_ID_TAG;
        SegmentBuffer buffer = new SegmentBuffer();
        buffer.setTag(tag);
        Segment segment = buffer.getCurrent();
        segment.setValue(new AtomicLong(0));
        segment.setMax(0);
        segment.setStep(0);
        IdCache.put(tag, buffer);
        logger.debug("cache init end. IdCache:[{}]", IdCache.toString());
        initSuccess = true;
        return initSuccess;
    }

    /**
     * 获取分布式 Id
     */
    public static long getId(String tag) throws InitException {
        if (!initSuccess) {
            throw new InitException("IdCache 未初始化");
        }
        if (IdCache.containsKey(tag)) {
            SegmentBuffer buffer = IdCache.get(tag);
            // buffer 未更新,先更新 buffer
            if (!buffer.isInitSuccess()) {
                synchronized (buffer) {
                    // 双重校验防止被其他线程更新了
                    if (!buffer.isInitSuccess()) {
                        try {
                            applySegmentFromDb(tag, buffer.getCurrent());
                            logger.debug("Init Tag:[{}] Buffer:[{}] From Db ", tag, buffer.getCurrent());
                            // buffer 初始化成功
                            buffer.setInitSuccess(true);
                        } catch (Exception e) {
                            logger.error("Init Tag:[" + tag + "] Buffer:[" + buffer.getCurrent() + "] From Db Exception", e);
                        }
                    }
                }
            }
            return getIdFromSegmentBuffer(IdCache.get(tag));
        } else {
            throw new NilTagException("IdCache 中不存在 tag:" + tag);
        }
    }

    /**
     * Apply Buffer Segment From Db
     */
    private static void applySegmentFromDb(String tag, Segment segment) {
        SegmentBuffer buffer = segment.getBuffer();
        Record record;
        if (!buffer.isInitSuccess()) {
            //未初始化
            // apply record
            // TODO
            record = new Record();
            // record = updateMaxIdAndGetUniqueRecord(tag);
            buffer.setUpdateTimestamp(System.currentTimeMillis());
            buffer.setStep(record.getStep());
            buffer.setMinStep(record.getStep());
        } else {
            // 当剩余ID不足会预更新另一个 Buffer Segment
            long duration = System.currentTimeMillis() - buffer.getUpdateTimestamp();
            int nextStep = buffer.getStep();
            //当更新时间小于 15 分钟，扩大步长，更新时间大于 30 分钟，缩小步长。 为了防止某时间段业务量飙升
            if (duration < SegmentConstant.SEGMENT_DURATION) {
                if (nextStep * 2 > SegmentConstant.MAX_STEP) {
                    nextStep = SegmentConstant.MAX_STEP;
                } else {
                    nextStep = nextStep * 2;
                }
            } else if (duration < SegmentConstant.SEGMENT_DURATION * 2) {
                // Do Nothing with nextStep
            } else {
                nextStep = nextStep / 2 >= buffer.getMinStep() ? nextStep / 2 : nextStep;
            }
            logger.debug("transform for tag[{}],step[{}],duration[{}mins],nextStep[{}]", tag, buffer.getStep(), String.format("%.2f", ((double) duration / (1000 * 60))), nextStep);
            // apply record
            // TODO
            record = new Record();
            // record = updateMaxIdByCustomStepAndGetLeafAlloc(tag, nextStep);
            buffer.setUpdateTimestamp(System.currentTimeMillis());
            buffer.setStep(nextStep);
            buffer.setMinStep(record.getStep());
        }
        // must set value before set max
        long value = record.getMaxId() - buffer.getStep();
        segment.getValue().set(value);
        segment.setMax(record.getMaxId());
        segment.setStep(buffer.getStep());
    }

    /**
     * 获取分布式 Id 核心代码，保证线程安全。
     *
     * @param buffer
     * @return Id
     * @throws InitException
     */
    private static long getIdFromSegmentBuffer(final SegmentBuffer buffer) throws InitException {
        // 循环，意在写锁内切换完 Segment，重新再读锁中获取 Id
        while (true) {
            //加读锁
            buffer.getReadLock().lock();
            try {
                final Segment segment = buffer.getCurrent();
                // buffer.getThreadRunning() 标识正在初始化 防止同时出现多个线程初始化
                if (!buffer.isNextReady() && (segment.getIdle() < segment.getStep() * 0.9)
                        && buffer.getThreadRunning().compareAndSet(false, true)) {
                    service.execute(() -> {
                        Segment next = buffer.getSegments()[buffer.nextPos()];
                        boolean updateOk = false;
                        try {
                            applySegmentFromDb(buffer.getTag(), next);
                            updateOk = true;
                            logger.debug("Tag:[{}] Update Buffer Segment From Db [{}]", buffer.getTag(), next);
                        } catch (Exception e) {
                            logger.error("Tag:[" + buffer.getTag() + "] Update Buffer Segment From Db Exception", e);
                        } finally {
                            if (updateOk) {
                                // 加写锁
                                buffer.getWriteLock().lock();
                                buffer.setNextReady(true);
                                buffer.getThreadRunning().set(false);
                                buffer.getWriteLock().unlock();
                            } else {
                                buffer.getThreadRunning().set(false);
                            }
                        }
                    });
                }
                long value = segment.getValue().getAndIncrement();
                if (value < segment.getMax()) {
                    return value;
                }
            } finally {
                buffer.getReadLock().unlock();
            }
            // 要切换 Segment 加写锁
            buffer.getWriteLock().lock();
            try {
                // 重新校验，防止已经被其他线程切换了
                final Segment segment = buffer.getCurrent();
                long value = segment.getValue().getAndIncrement();
                if (value < segment.getMax()) {
                    return value;
                }
                if (buffer.isNextReady()) {
                    buffer.switchPos();
                    buffer.setNextReady(false);
                } else {
                    logger.error("Tag:[{}] Both Two Buffer Segments In [{}] Are Not Ready!", buffer.getTag(), buffer);
                    throw new InitException("SegmentBuffer 中的两个 Segment 均未从 DB 中装载");
                }
            } finally {
                buffer.getWriteLock().unlock();
            }
        }
    }
}