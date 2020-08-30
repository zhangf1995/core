package zf.core.sentinel.limit.interceptor;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import zf.core.sentinel.limit.SlidingWindow;

@Configuration
public class InterceptorConfig extends WebMvcConfigurerAdapter {

    @Autowired
    private SlidingWindow slidingWindow;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SentinelInterceptor(slidingWindow)).addPathPatterns("/sentinel/**");
    }
}
