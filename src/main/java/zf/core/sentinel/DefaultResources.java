package zf.core.sentinel;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Data
public class DefaultResources implements Resources {

    private String name;

    private boolean isFusing = false;

    private static final int THRESHOLD = 5;

    private static ScheduledExecutorService scheduleExec = Executors.newScheduledThreadPool(
            Runtime.getRuntime().availableProcessors(), new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r);
                    thread.setName("SCHEDULE-OPENFUSING-THREAD");
                    thread.setDaemon(true);
                    return thread;
                }
            });


    //单位时间请求总数
    private AtomicInteger totalRequest = new AtomicInteger(0);

    private AtomicInteger currentRequest = new AtomicInteger(0);

    //单位时间失败请求数
    private AtomicInteger failNum = new AtomicInteger(0);

    //单位时间响应时间
    private long responseTime;

    //规则
    private FlowRule rule;

    //unit:s
    private Integer scheduleTime = 60;

    private Object lock = new Object();


    public DefaultResources(String name, FlowRule rule) {
        this.name = name;
        this.rule = rule;
    }

    public boolean containt(String resourceName) {
        return false;
    }

    public boolean isFusing() {
        if (isFusing) {
            return true;
        }
        modifyIsFusing();
        return isFusing;
    }

    private void modifyIsFusing() {
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (currentRequest.incrementAndGet() >= THRESHOLD) {
            if (failNum.get() / totalRequest.get() >= rule.getFailRate()) {
                synchronized (lock) {
                    if (!isFusing) {
                        isFusing = true;
                        //open fusing,schedule open
                        ScheduleOpenTask task = new ScheduleOpenTask(this);
                        scheduleExec.schedule(task, scheduleTime, TimeUnit.SECONDS);
                    }
                }
            }
        }
    }

    public void cacuLateRule(boolean isSuccess, long responseTime) {
        totalRequest.incrementAndGet();
        switch (rule.getRuletype()) {
            case AVGRESPONSE:
                modifyAvg(responseTime);
                break;
            case FALIRATE:
                modifyRate(isSuccess);
                break;
        }
        //notify
    }

    private void modifyAvg(long responseTime) {
    }

    private void modifyRate(boolean isSuccess) {
        if (!isSuccess) {
            failNum.incrementAndGet();
        }
    }

    public void reset() {
        this.isFusing = false;
        this.totalRequest = new AtomicInteger(0);
        this.currentRequest = new AtomicInteger(0);
        this.failNum = new AtomicInteger(0);
    }


    class ScheduleOpenTask implements Runnable {

        private DefaultResources resources;

        public ScheduleOpenTask(DefaultResources resources) {
            this.resources = resources;
        }

        @Override
        public void run() {
            synchronized (resources) {
                resources.reset();
            }
        }
    }
}
