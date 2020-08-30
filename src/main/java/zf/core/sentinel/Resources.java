package zf.core.sentinel;

public interface Resources {

    boolean containt(String resourceName);

    boolean isFusing();

    String getName();

    FlowRule getRule();

    void cacuLateRule(boolean isSuccess, long responseTime);
}
