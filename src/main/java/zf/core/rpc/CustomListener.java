package zf.core.rpc;

import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;

import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

@Component
public class CustomListener implements ApplicationListener<WebServerInitializedEvent> {

    @NacosInjected
    private NamingService namingService;

    private Map<String, List<Instance>> allInstances = new HashMap<>();

    private Set<CustomRpcFactoryBean> customRpcFactoryBeans = new HashSet<>();

    private static final Integer DEFAULT_PULL_INTERVAL = 30;



    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {
        //开启一个定时线程，定时去获取注册中心状态以及更新服务及实例

        ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setDaemon(true);
                thread.setName("CUSOTM-THREAD-SCHEDULE");
                return thread;
            }
        });
        init(event);
        exec.scheduleWithFixedDelay(new PullInstance(event),
                DEFAULT_PULL_INTERVAL, DEFAULT_PULL_INTERVAL, TimeUnit.SECONDS);

    }

    class PullInstance implements Runnable {

        private WebServerInitializedEvent event;

        public PullInstance(WebServerInitializedEvent event) {
            this.event = event;
        }

        @Override
        public void run() {
            String serverStatus = namingService.getServerStatus();
            if ("UP".equals(serverStatus)) {
                if (ObjectUtils.isEmpty(customRpcFactoryBeans)) {
                    init(event);
                } else {
                    customRpcFactoryBeans.forEach(customRpcFactoryBean -> {
                        modifyInstance(customRpcFactoryBean);
                    });
                }
            } else {
                //所有服务端实例全挂掉，这里指的是nacos
            }
        }
    }

    private void init(WebServerInitializedEvent event) {
        Map<String, CustomRpcFactoryBean> beansOfType = event.getApplicationContext().getBeansOfType(CustomRpcFactoryBean.class);
        for (Map.Entry<String, CustomRpcFactoryBean> entry : beansOfType.entrySet()) {
            customRpcFactoryBeans.add(entry.getValue());
            modifyInstance(entry.getValue());
        }

    }

    private void modifyInstance(CustomRpcFactoryBean customRpcFactoryBean) {
        try {
            List<Instance> existInstance;
            List<Instance> instances = namingService.getAllInstances(customRpcFactoryBean.getName());
            if (!ObjectUtils.isEmpty(existInstance = allInstances.get(customRpcFactoryBean.getName()))) {
                existInstance.addAll(instances);
            }else{
                existInstance = new ArrayList<>(instances);
            }
            allInstances.put(customRpcFactoryBean.getName(), existInstance);
            //sync cusotmfactorybean
            customRpcFactoryBean.refresh(existInstance);
        } catch (NacosException e) {
            e.printStackTrace();
        }
    }

    public List<Instance> getAllInstance(String serviceName) {
        List<Instance> instances = null;
        try {
            instances = allInstances.get(serviceName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return instances;
    }
}
