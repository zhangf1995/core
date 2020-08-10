package zf.core.rpc;

import com.alibaba.nacos.api.naming.pojo.Instance;
import lombok.Data;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.client.RestTemplate;
import zf.core.rpc.invoke.CustomInvokeHandle;

import java.lang.reflect.Proxy;
import java.util.List;

@Data
public class CustomRpcFactoryBean<T> implements FactoryBean<T>, ApplicationContextAware, ApplicationRunner {

    private String name;

    private Class type;

    private String url;

    private String path;

    private String serviceId;

    private Class fallback;

    private CustomInvokeHandle customInvokeHandle;

    private ApplicationContext applicationContext;

    private RestTemplate restTemplate;

    private CustomListener customListener;


    @Override
    public T getObject() throws Exception {
        return target();
    }

    public <T> T target() throws Exception {
        this.url = "http://";
        this.customInvokeHandle = new CustomInvokeHandle(url, type, restTemplate);
        return doTarget();
    }

    private <T> T doTarget() {
        return (T) Proxy.newProxyInstance(type.getClassLoader(),
                new Class[]{type}, customInvokeHandle);
    }

    @Override
    public Class<?> getObjectType() {
        return this.type;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        this.restTemplate = applicationContext.getBean("customRestTemplate", RestTemplate.class);
    }


    @Override
    public void run(ApplicationArguments args) throws Exception {
        //以后拓展用
    }

    public void refresh(List<Instance> existInstance) {
        customInvokeHandle.refresh(existInstance);
    }
}
