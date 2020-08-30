package zf.core.sentinel.limit;

import lombok.Data;

import java.util.concurrent.atomic.AtomicInteger;

@Data
public class WindowWrap {
    private long startTime;

    private AtomicInteger count = new AtomicInteger(0);

    private int limit;

    public WindowWrap(long startTime) {
        this.startTime = startTime;
    }

    public WindowWrap(long startTime, int limit) {
        this.startTime = startTime;
        this.limit = limit;
    }

    public boolean beyondLimit(){
        return count.incrementAndGet() > limit ? Boolean.TRUE : Boolean.FALSE;
    }
}
