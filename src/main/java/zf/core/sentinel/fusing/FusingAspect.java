package zf.core.sentinel.fusing;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import zf.core.sentinel.*;

import java.lang.reflect.Method;

@Slf4j
@Aspect
public class FusingAspect {

    @Pointcut("@annotation(zf.core.sentinel.fusing.FusingResource)")
    public void pointCut() {
    }

    @Around("pointCut()")
    public Object handle(ProceedingJoinPoint pjp) {
        Resources frs = null;
        long responseTime;
        long requestTime = System.currentTimeMillis();
        try {
            //before
            Signature signature = pjp.getSignature();
            if (!(signature instanceof MethodSignature)) {
                throw new Exception("@FusingResource must be applied on method");
            }
            MethodSignature ms = (MethodSignature) signature;
            Method method = pjp.getTarget().getClass().getMethod(ms.getName(), ms.getParameterTypes());
            FusingResource fusingResource = method.getAnnotation(FusingResource.class);
            frs = getResources(frs, fusingResource);
            //need fusing,fallback
            if (ResourcesUtils.isFusing(frs)) {
                Class<FallBack> fallback = (Class<FallBack>) fusingResource.fallback();
                return fallback.newInstance().fallback();
            } else {
                //process
                Object proceed = pjp.proceed();
                //after success
                responseTime = System.currentTimeMillis() - requestTime;
                ResourcesUtils.modifyAndIsFusing(frs.getName(), true, responseTime);
                return proceed;
            }
        } catch (Throwable throwable) {
            //fail
            responseTime = System.currentTimeMillis() - requestTime;
            ResourcesUtils.modifyAndIsFusing(frs.getName(), false, responseTime);
            throwable.printStackTrace();
        }
        return null;
    }

    private Resources getResources(Resources frs, FusingResource fusingResource) {
        //这块可以放在register那把所有resource的put到map中
        while (null == frs) {
            if (!ResourcesUtils.resourcesMap.containsKey(fusingResource.value())) {
                //default rule failRate
                DefaultRule rule = new DefaultRule(RuleType.FALIRATE);
                DefaultResources resources = new DefaultResources(fusingResource.value(), rule);
                ResourcesUtils.setRule(resources);
            } else {
                frs = ResourcesUtils.resourcesMap.get(fusingResource.value());
            }
        }
        return frs;
    }
}
