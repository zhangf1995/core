package zf.core.rpc.invoke;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Method;
import java.util.Map;

@Slf4j
@Data
public class DefaultMethodHandle extends CustomMethodHandle {

    private static final Integer RESPONSE_OK = 200;

    public DefaultMethodHandle(Method method, RestTemplate restTemplate,
                               Class type, String url) throws Exception {
        super(method, restTemplate, type, url);
    }

    //?id=id&id1=id1&id2=id2
    @Override
    public Object invoke(Object[] args) throws Exception {
        super.invoke(args);
        Map<String, Object> uriVariables = getTargetRequest().getParam(args);
        return sendHttpRequest(uriVariables);
    }


    private Object sendHttpRequest(Map<String, Object> uriVariables) throws Exception {
        ResponseEntity<?> entity;
        String wholeUrl = getTargetRequest().splitWholeUrl(uriVariables);
        switch (getRequestMethod()) {
            case "GET":
                entity = getRestTemplate().getForEntity(wholeUrl, getMethod().getReturnType(), uriVariables);
                break;
            case "POST":
                entity = getRestTemplate().postForEntity(wholeUrl, uriVariables, getMethod().getReturnType());
                break;
            //.....
            default:
                throw new Exception("");
                //
        }
        if (RESPONSE_OK != entity.getStatusCodeValue()) {
            // 自己写
        }
        return entity.getBody();
    }
}
