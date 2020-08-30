package zf.core.sentinel.limit.interceptor;


import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import zf.core.sentinel.limit.SlidingWindow;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.locks.ReentrantLock;

public class SentinelInterceptor implements HandlerInterceptor {

    private SlidingWindow slidingWindow;

    private ReentrantLock lock = new ReentrantLock();

    public SentinelInterceptor(SlidingWindow slidingWindow) {
        this.slidingWindow = slidingWindow;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!slidingWindow.beyondLimit(System.currentTimeMillis())) {
            return true;
        } else {
            //可以直接抛出异常去处理，也可以等待其他请求线程结束,这地方可以做定制(加个规则)
            //这里面就简单写一下
            try{
                response.setContentType("text/html;charset=utf-8");
                response.getWriter().write("我被限流了");
                response.getWriter().flush();
            }finally {
                response.getWriter().close();
            }
            return false;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
