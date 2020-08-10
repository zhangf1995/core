package zf.core.rpc.annotation;

import org.springframework.context.annotation.Import;
import zf.core.rpc.CustomRpcRegister;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(CustomRpcRegister.class)
public @interface CustomEnableRpc {
    String basePackage() default "";
}
