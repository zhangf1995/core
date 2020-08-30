package zf.core.annoValue;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomAnnotationController {

    @CustomValue(value = "${c}")
    private String c;

/*    @Value("${zhi}")
    private String zhi;*/

}
