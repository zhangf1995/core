package zf.core.sentinel.fusing;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class FusingTestController {

    @FusingResource(value = "fushingTest", fallback = FusingBack.class)
    @RequestMapping("/fusingTest")
    public String fusingTest(String a) {
        if(a.equals("0")){
            System.out.println("fail request");
            int i = 1 / 0;
        }
        System.out.println("success request");
        return a;
    }

}
