package zf.core.annoValue;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Component
public class CusotomPropertySourcePostProcess implements BeanDefinitionRegistryPostProcessor, EnvironmentAware {

    private ConfigurableEnvironment environment;



    @Override
    public void setEnvironment(Environment environment) {
        this.environment = (ConfigurableEnvironment) environment;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        MutablePropertySources propertySources = environment.getPropertySources();
        Properties properties = new Properties();
        Map<String,String> map = new HashMap<>();
        map.put("c","10");
        properties.putAll(map);
        CustomPropertySource propertySource = new CustomPropertySource("customPropertySource",properties);
        propertySources.addLast(propertySource);
    }
}
