package zf.core.sentinel;

import lombok.Data;

@Data
public class DefaultRule extends FlowRule {

    public DefaultRule(RuleType ruleType) {
        this.ruletype = ruleType;
    }

    public void setFailRate(double failRate) {
        this.failRate = failRate;
    }

    public void setAvgResponse(long avgResponse) {
        this.avgResponse = avgResponse;
    }
}
