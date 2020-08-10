package zf.core.rpc;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
@Slf4j
public class TargetRequest {
    private  Class type;
    private Method method;
    private  String url;
    private String requestMethod;
    private Parameter [] parameters;

    public TargetRequest(Class type, Method method, String url,
                         String requestMethod,Parameter [] parameters) {
        this.type = type;
        this.requestMethod = requestMethod;
        this.url = url;
        this.method = method;
        this.parameters = parameters;
    }

    public LinkedHashMap<String, Object> getParam(Object [] args) {
        LinkedHashMap<String,Object> valueMap = new LinkedHashMap<>();
        for (int i = 0; i < parameters.length; i++) {
            if(null != args[i]){
                valueMap.put(parameters[i].getName(), args[i]);
            }
        }
        return valueMap;
    }


    public String splitWholeUrl(Map<String, Object> uriVariables){
        String wholeUrl = this.url;
        switch (requestMethod){
            case "GET":
                wholeUrl = doSplitWholeUrl(uriVariables);
                break;
        }
        log.info("url {},requestmethod {},paramter {}", wholeUrl, this.requestMethod, JSONObject.toJSONString(uriVariables));
        return wholeUrl;
    }

    private String doSplitWholeUrl(Map<String, Object> uriVariables) {
        String wholeUrl;
        StringBuffer sb = new StringBuffer();
        sb.append(getUrl()).append("?");
        uriVariables.forEach((key, value) -> {
            sb.append(key).append("={").append(key).append("}&");
        });
        wholeUrl = sb.substring(0, sb.length() - 1).toString();
        return wholeUrl;
    }
}
