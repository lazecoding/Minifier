package lazecoding.minifier.mapper;

import lazecoding.minifier.model.Record;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * SegmentRecordMapper
 *
 * @author lazecoding
 */
@Mapper
public interface SegmentRecordMapper {

    /**
     * 更新号段，step 取默认值
     *
     * @param tag tag
     */
    void updateMaxId(@Param("tag") String tag);

    /**
     * 获取 Record
     *
     * @param tag tag
     * @return Record
     */
    Record getRecord(@Param("tag") String tag);

    /**
     * 更新号段，自定义 step
     *
     * @param tag  tag
     * @param step 步长
     */
    void updateMaxIdByCustomStep(@Param("tag") String tag, @Param("step") int step);
}
