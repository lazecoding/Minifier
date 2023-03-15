package lazecoding.minifier.constant;

/**
 *
 * Segment 常量
 *
 * @author lazecoding
 */
public class SegmentConstant {


    /**
     * 最大步长
     */
    public static final int MAX_STEP = 1000000;

    /**
     * 最小步长
     */
    public static final int MIN_STEP = 20000;

    /**
     * 一个 Segment 维持时间为 15 分钟
     */
    public static final long SEGMENT_DURATION = 15 * 60 * 1000L;

    /**
     * 短链 Id Tag
     */
    public static final String SHORT_ID_TAG = "minifier_url_id_tag";

}
