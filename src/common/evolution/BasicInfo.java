package common.evolution;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 1, 2010
 * Time: 4:44:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class BasicInfo {
    protected Map<String, Object> infoMap = new LinkedHashMap<String, Object>();

    public BasicInfo() {
    }

    public BasicInfo(Map<String, Object> infoMap) {
        this.infoMap.putAll(infoMap);
    }

    public Object getInfo(String name) {
        return infoMap.get(name);
    }
}