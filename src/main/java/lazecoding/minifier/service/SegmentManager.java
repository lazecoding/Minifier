package lazecoding.minifier.service;

import lazecoding.minifier.mapper.SegmentRecordMapper;
import lazecoding.minifier.model.Record;
import lazecoding.minifier.util.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * SegmentManager
 *
 * @author lazecoding
 */
@Component("segmentManager")
public class SegmentManager {

    @Autowired
    private SegmentRecordMapper segmentRecordMapper;

    /**
     * 获取实例
     */
    public static SegmentManager getInstance() {
        return BeanUtil.getBean(SegmentManager.class);
    }

    /**
     * 申请号段
     */
    @Transactional(rollbackFor = Exception.class)
    public Record updateMaxIdAndGetRecord(String tag) {
        segmentRecordMapper.updateMaxId(tag);
        return segmentRecordMapper.getRecord(tag);
    }

    /**
     * 申请号段（自定义步长）
     */
    @Transactional(rollbackFor = Exception.class)
    public Record updateMaxIdByCustomStepAndGetLeafAlloc(String tag, int step) {
        segmentRecordMapper.updateMaxIdByCustomStep(tag, step);
        return segmentRecordMapper.getRecord(tag);
    }

}