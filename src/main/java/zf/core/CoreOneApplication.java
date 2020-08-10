package zf.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import zf.core.rpc.annotation.CustomEnableRpc;

@CustomEnableRpc
@SpringBootApplication
public class CoreOneApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoreOneApplication.class, args);
    }

}
