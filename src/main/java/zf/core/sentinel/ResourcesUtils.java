package zf.core.sentinel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ResourcesUtils {

    public static final Map<String, Resources> resourcesMap = new ConcurrentHashMap<>(256);

    public static boolean setRule(DefaultResources resources) {
        try {
            synchronized (resources) {
                if (!resourcesMap.containsKey(resources.getName())) {
                    resourcesMap.put(resources.getName(), resources);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean isFusing(Resources resources) {
        return resourcesMap.containsKey(resources.getName()) ? resources.isFusing() : Boolean.FALSE;
    }

    public static void modifyAndIsFusing(String resourceName, boolean isSuccess, long responseTime) {
        Resources resources = null;
        if (null != (resources = resourcesMap.get(resourceName))) {
            switch (resources.getRule().getRuletype()) {
                case AVGRESPONSE:
                case FALIRATE:
                    resources.cacuLateRule(isSuccess, responseTime);
                    break;
                default:
                    //custom rule
            }
        }
    }
}
