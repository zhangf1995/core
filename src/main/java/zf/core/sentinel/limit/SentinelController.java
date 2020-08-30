package zf.core.sentinel.limit;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/sentinel")
public class SentinelController {

    AtomicInteger count = new AtomicInteger(0);

    @RequestMapping("/test")
    public String test() {
        System.out.println("当前时间:"+System.currentTimeMillis()+",数量:"+count.get());
        return "我进来了,count是";
    }
}
