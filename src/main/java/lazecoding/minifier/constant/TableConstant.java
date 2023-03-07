package lazecoding.minifier.constant;

/**
 * TableConstant
 *
 * @author lazecoding
 */
public class TableConstant {

    /**
     * 表头
     */
    private static String URL_MAP_HEAD = "url_map_";

    /**
     * 分表总个数
     */
    private static int TOTAL = 128;

    /**
     * 获取表名
     *
     * @param code 短码
     * @return 表名
     */
    public static String getUrlMapTable(String code) {
        int hash = hash(code) & (TOTAL - 1);
        return URL_MAP_HEAD + hash;
    }

    /**
     * hash 散列均匀
     */
    private static int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

}
