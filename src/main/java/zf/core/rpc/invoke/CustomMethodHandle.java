package zf.core.rpc.invoke;

import com.alibaba.nacos.api.naming.pojo.Instance;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import zf.core.rpc.TargetRequest;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

@Slf4j
@Data
public class CustomMethodHandle {

    private Method method;

    private RestTemplate restTemplate;

    private Class target;

    private String url;

    private String value;

    private String path;

    private TargetRequest targetRequest;

    private String requestMethod;

    protected List<Instance> instances;

    public CustomMethodHandle(Method method, RestTemplate restTemplate,
                              Class target, String url) throws Exception {
        this.method = method;
        this.restTemplate = restTemplate;
        this.target = target;
        this.url = url;
        init();
    }

    private void init() throws Exception {
        Annotation[] annotations = method.getDeclaredAnnotations();
        if (ObjectUtils.isEmpty(annotations) || ObjectUtils.isEmpty(method.getAnnotation(RequestMapping.class))) {
            throw new Exception(method.getName() + " must have @RequestMapping in " + target.getName());
        } else {
            for (Annotation annotation : annotations) {
                if (ClassUtils.isAssignable(RequestMapping.class, annotation.getClass())) {
                    RequestMapping requestMapping = (RequestMapping) annotation;
                    setTargetRequest(requestMapping);
                } else {
                    //custom add
                }
            }
        }
    }

    private void setTargetRequest(RequestMapping requestMapping) throws Exception {
        String requestMethod;
        //不考虑默认value= /index
        Assert.isTrue(!StringUtils.isEmpty(this.value = requestMapping.value()[0]),
                target + " have " + method + " @RequestMapping value must assignment");
        if (requestMapping.method().length == 0) {
            requestMethod = "GET";
        } else {
            requestMethod = String.valueOf(requestMapping.method()[0]);
        }
        this.requestMethod = requestMethod;
        this.path = requestMapping.path().length > 0 ? requestMapping.path()[0] : "";
        this.targetRequest = new TargetRequest(target, method,
                url, requestMethod, method.getParameters());
    }

    public Object invoke(Object[] args) throws Exception {
        log.info("customRpc,target:{},invokemethod:{}", target.getName(), method.getName());

        StringBuffer sb = new StringBuffer();

        Instance instance = getCUrrentInstance();

        this.url = sb.append(this.url).append(instance.getIp()).
                append(":").append(instance.getPort()).append(this.value).toString();
        targetRequest.setUrl(this.url);
        //以后做扩展
        return null;
    }

    //默认的
    protected Instance getCUrrentInstance() {
        return instances.get(0);
    }

    public void refresh(List<Instance> instances) throws Exception {
        this.instances = instances;
    }
}
