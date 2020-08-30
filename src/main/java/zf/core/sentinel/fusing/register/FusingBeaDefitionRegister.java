package zf.core.sentinel.fusing.register;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import zf.core.sentinel.fusing.EnableFusing;
import zf.core.sentinel.fusing.FusingAspect;

import java.util.Map;

public class FusingBeaDefitionRegister implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        Map<String, Object> attr = importingClassMetadata.getAnnotationAttributes(EnableFusing.class.getName());
        if (openFusing(attr)) {
            //register
            BeanDefinitionBuilder bd = BeanDefinitionBuilder.genericBeanDefinition(FusingAspect.class);
            registry.registerBeanDefinition("fusingAspect",bd.getBeanDefinition());
        }
    }

    private boolean openFusing(Map<String, Object> attr) {
        boolean openFusing = false;
        for (String key : attr.keySet()) {
            if(key.equals("openFusing")){
                openFusing = (boolean) attr.get(key);
            }
        }
        return openFusing;
    }

    protected ClassPathScanningCandidateComponentProvider getScanner() {
        return new ClassPathScanningCandidateComponentProvider(false) {
            @Override
            protected boolean isCandidateComponent(
                    AnnotatedBeanDefinition beanDefinition) {
                boolean isCandidate = false;
                if (beanDefinition.getMetadata().isIndependent()) {
                    if (!beanDefinition.getMetadata().isAnnotation()) {
                        isCandidate = true;
                    }
                }
                return isCandidate;
            }
        };
    }
}
