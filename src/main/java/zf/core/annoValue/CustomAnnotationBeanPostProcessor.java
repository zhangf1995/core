package zf.core.annoValue;

import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CustomAnnotationBeanPostProcessor extends InstantiationAwareBeanPostProcessorAdapter
         implements MergedBeanDefinitionPostProcessor, BeanFactoryAware {

    private ConfigurableBeanFactory beanFactory;

    private final Map<String, InjectionMetadata> injectionMetadataCache = new ConcurrentHashMap<>(256);

    @Override
    public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
        InjectionMetadata metadata = findInjectionMetadata(beanName, beanType, null);
        metadata.checkConfigMembers(beanDefinition);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (ConfigurableBeanFactory) beanFactory;
    }


    @Override
    public PropertyValues postProcessPropertyValues(PropertyValues pvs, PropertyDescriptor[] pds,
                                                    Object bean, String beanName) throws BeansException {
        InjectionMetadata injectionMetadata = findInjectionMetadata(beanName, bean.getClass(), pvs);
        try {
            injectionMetadata.inject(bean,beanName,pvs);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return pvs;
    }

    private InjectionMetadata findInjectionMetadata(String beanName, Class<?> clazz, @Nullable PropertyValues pvs) {
        // Fall back to class name as cache key, for backwards compatibility with custom callers.
        String cacheKey = (StringUtils.hasLength(beanName) ? beanName : clazz.getName());
        // Quick check on the concurrent map first, with minimal locking.
        InjectionMetadata metadata = this.injectionMetadataCache.get(cacheKey);
        if (InjectionMetadata.needsRefresh(metadata, clazz)) {
            synchronized (this.injectionMetadataCache) {
                metadata = this.injectionMetadataCache.get(cacheKey);
                if (InjectionMetadata.needsRefresh(metadata, clazz)) {
                    if (metadata != null) {
                        metadata.clear(pvs);
                    }
                    metadata = buildInjectionMetadata(clazz);
                    this.injectionMetadataCache.put(cacheKey, metadata);
                }
            }
        }
        return metadata;
    }


    private InjectionMetadata buildInjectionMetadata(Class<?> clazz) {
        return getMetedata(clazz);
    }

    private CustomInjectionMetedata getMetedata(Class<?> clazz) {

        Class<?> targetClass = clazz;

        List<InjectionMetadata.InjectedElement> elementList = new ArrayList<>();

        ReflectionUtils.doWithLocalFields(targetClass, field -> {
            CustomValue annotation = AnnotationUtils.getAnnotation(field, CustomValue.class);
            if(null != annotation){
                elementList.add(new CustomElement(field,annotation));
            }
        });
        return new CustomInjectionMetedata(targetClass,elementList);
    }

    class CustomInjectionMetedata extends InjectionMetadata {

;

        /**
         * Create a new {@code InjectionMetadata instance}.
         * <p>Preferably use {@link #forElements} for reusing the {@link #EMPTY}
         * instance in case of no elements.
         *
         * @param targetClass the target class
         * @param elements    the associated elements to inject
         * @see #forElements
         */
        public CustomInjectionMetedata(Class<?> targetClass, Collection<InjectedElement> elements) {
            super(targetClass, elements);
        }
    }


    class CustomElement extends InjectionMetadata.InjectedElement{

        private Field field;

        private Annotation annotation;

        protected CustomElement(Field field,Annotation annotation) {
            super(field, null);
            this.field = field;
            this.annotation = annotation;
        }

        @Override
        public void inject(Object target, String beanName, PropertyValues pvs) throws Throwable {
            Class<?> type = field.getType();
            Object injectObject = resolver((CustomValue) annotation);
            ReflectionUtils.makeAccessible(field);
            field.set(target, injectObject);
        }

        public Object resolver(CustomValue annotation) {
            String value = annotation.value();
            String realValue = beanFactory.resolveEmbeddedValue(value);
            return realValue;
        }
    }

}
