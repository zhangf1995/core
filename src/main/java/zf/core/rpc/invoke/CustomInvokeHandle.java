package zf.core.rpc.invoke;

import com.alibaba.nacos.api.naming.pojo.Instance;
import lombok.Data;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class CustomInvokeHandle implements InvocationHandler {

    //method map
    private Map<Method, CustomMethodHandle> methods = new HashMap<>();
    private Class type;
    protected String url;
    private RestTemplate restTemplate;

    public CustomInvokeHandle(String url, Class type, RestTemplate restTemplate) throws Exception {
        this.url = url;
        this.type = type;
        this.restTemplate = restTemplate;
        initMethod();
    }

    private void initMethod() throws Exception {
        //get method,init invokecationhandle
        for (Method method : type.getMethods()) {
            //filter object method,example toString(),hasCode()...
            if (method.getDeclaringClass() == Object.class) {
                continue;
            }
            this.methods.put(method, new DefaultMethodHandle(method, restTemplate, type, url));
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
        return methods.get(method).invoke(args);
    }

    public void refresh(List<Instance> existInstance) {
        methods.forEach((method, customMethodHandle) -> {
            try {
                customMethodHandle.refresh(existInstance);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}
