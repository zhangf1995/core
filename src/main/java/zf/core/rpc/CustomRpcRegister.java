package zf.core.rpc;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import zf.core.rpc.annotation.CustomEnableRpc;
import zf.core.rpc.annotation.CustomRpc;

import java.util.Map;
import java.util.Set;

public class CustomRpcRegister implements ImportBeanDefinitionRegistrar, ResourceLoaderAware{

    private ResourceLoader resourceLoader;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        registryRpcClients(importingClassMetadata, registry);
    }

    private void registryRpcClients(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        Map<String, Object> attr = importingClassMetadata.getAnnotationAttributes(CustomEnableRpc.class.getName());
        //get basepackage
        String basePackage = getBasePackage(importingClassMetadata, attr);
        //define scanner
        ClassPathScanningCandidateComponentProvider scanner = getScanner();
        AnnotationTypeFilter filter = new AnnotationTypeFilter(CustomRpc.class);
        scanner.addIncludeFilter(filter);
        scanner.setResourceLoader(this.resourceLoader);
        Set<BeanDefinition> candidateComponents = scanner.findCandidateComponents(basePackage);
        if (!ObjectUtils.isEmpty(candidateComponents)) {
            candidateComponents.forEach(candidateComponent -> {
                if (candidateComponent instanceof AnnotatedBeanDefinition) {
                    AnnotatedBeanDefinition abd = (AnnotatedBeanDefinition) candidateComponent;
                    AnnotationMetadata metadata = abd.getMetadata();
                    //can only interface
                    Assert.isTrue(metadata.isInterface(),
                            "@CustomRpc can only act on interfce");
                    //get attr
                    Map<String, Object> rpcAttr = metadata.getAnnotationAttributes(CustomRpc.class.getName());
                    Assert.isTrue(!StringUtils.isEmpty((String) rpcAttr.get("rpcName")),
                            "@CustomRpc property rpcName must have a value");
                    registryRpcClient(registry, metadata, rpcAttr);
                }
            });
        }
    }

    private void registryRpcClient(BeanDefinitionRegistry registry, AnnotationMetadata metadata, Map<String, Object> rpcAttr) {
        String rpcName = String.valueOf(rpcAttr.get("rpcName"));
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(CustomRpcFactoryBean.class);
        setBuilder(metadata, rpcAttr, rpcName, builder);
        AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
        beanDefinition.setPrimary((boolean) rpcAttr.get("primary"));
        //if restTemplate not exist,register restTemplate;
        registyRestTemplateIfAbsent(registry);
        registry.registerBeanDefinition(metadata.getClassName(), beanDefinition);
    }

    private void registyRestTemplateIfAbsent(BeanDefinitionRegistry registry) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(RestTemplate.class);
        registry.registerBeanDefinition("customRestTemplate",builder.getBeanDefinition());
    }

    private void setBuilder(AnnotationMetadata metadata, Map<String, Object> rpcAttr, String rpcName, BeanDefinitionBuilder builder) {
        builder.addPropertyValue("name", rpcName);
        builder.addPropertyValue("type", metadata.getClassName());
        builder.addPropertyValue("path", rpcAttr.get("path"));
        builder.addPropertyValue("serviceId", rpcAttr.get("serviceId"));
        builder.addPropertyValue("url", rpcAttr.get("url"));
        builder.addPropertyValue("fallback", rpcAttr.get("fallback"));
    }

    private String getQualifier(Map<String, Object> client) {
        if (client == null) {
            return null;
        }
        String qualifier = (String) client.get("qualifier");
        if (StringUtils.hasText(qualifier)) {
            return qualifier;
        }
        return null;
    }

    private String getBasePackage(AnnotationMetadata importingClassMetadata, Map<String, Object> attr) {
        String basePackage;
        if (StringUtils.isEmpty(basePackage = (String) attr.get("basePackage"))) {
            basePackage = ClassUtils.getPackageName(importingClassMetadata.getClassName());
        }
        return basePackage;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
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
