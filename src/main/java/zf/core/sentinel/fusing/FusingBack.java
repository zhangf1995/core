package zf.core.sentinel.fusing;

public class FusingBack implements FallBack{
    @Override
    public Object fallback() {
        return "这是降级方法";
    }
}
