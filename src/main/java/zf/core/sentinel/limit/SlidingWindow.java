package zf.core.sentinel.limit;

import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class SlidingWindow implements EnvironmentAware {

    private Environment environment;

    private ReentrantLock lock;

    //ms
    private static final long windowLeight = 500;

    private int limit;

    WindowWrap[] arrays;

    public SlidingWindow() {
    }

    @PostConstruct
    private void init() {
        this.lock = new ReentrantLock();
        this.arrays = new WindowWrap[2];
        Integer limit = environment.getProperty("sentinle.qps", Integer.class);
        this.limit = limit;
    }

    public boolean beyondLimit(long time) {
        return currentWindow(time).beyondLimit();
    }

    public WindowWrap currentWindow(long time) {
        System.out.println("tinme is " + time);
        long timeId = time / windowLeight;

        int index = (int) (timeId % arrays.length);

        long currentStartTime = time - time % windowLeight;

        while (true) {
            WindowWrap windowWrap = arrays[index];
            if (null == windowWrap) {
                WindowWrap currentWindowWrap = new WindowWrap(currentStartTime, limit);
                if (lock.tryLock()) {
                    try {
                        arrays[index] = currentWindowWrap;
                        return currentWindowWrap;
                    } finally {
                        lock.unlock();
                    }
                } else {
                    Thread.yield();
                }
            } else if (windowWrap.getStartTime() == currentStartTime) {
                return windowWrap;
            } else if (currentStartTime > windowWrap.getStartTime()) {
                if (lock.tryLock()) {
                    try {
                        return reset(currentStartTime, index);
                    } finally {
                        lock.unlock();
                    }
                } else {
                    Thread.yield();
                }
            }
        }
    }

    private WindowWrap reset(long currentStartTime, int index) {
        WindowWrap windowWrap = new WindowWrap(currentStartTime, limit);
        arrays[index] = windowWrap;
        return windowWrap;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
