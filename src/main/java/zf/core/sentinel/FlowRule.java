package zf.core.sentinel;

import lombok.Data;

@Data
public class FlowRule {
    protected long unitTime = 1000;
    protected RuleType ruletype;
    protected double failRate = 0.5;
    protected long avgResponse;

    public void setUnitTime(long time) {
        this.unitTime = time;
    }
}
