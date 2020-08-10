package zf.core.rpc;


import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

@Component
public class CustomCheckRegistry {

    private boolean registryHealthy;

    private List<String> registryCluster = new ArrayList<>();

    private ScheduledExecutorService executorService;

    public CustomCheckRegistry() {
        this.registryHealthy = false;
        this.executorService = new ScheduledThreadPoolExecutor(2, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setDaemon(true);
                thread.setName("CUSTOM_REGISTRY-");
                return thread;
            }
        });
    }

    class CheckRegistry implements Runnable {

        //private NamingService namingService;

/*        public CheckRegistry(NamingService namingService) {
            this.namingService = namingService;
        }*/

        @Override
        public void run() {
           /* String serverStatus = namingService.getServerStatus();
            if ("DOWN".equals(serverStatus)) {
                //registry unhealthy
                registryHealthy = false;
            } else {
                //registry healthy,pull service list

            }*/
        }
    }
}
