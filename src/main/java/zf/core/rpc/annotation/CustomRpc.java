package zf.core.rpc.annotation;

import javax.validation.constraints.NotNull;
import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CustomRpc {
    @NotNull(message = "rpcName must have a value")
    String rpcName();

    String url() default "";

    String serviceId() default "";

    String path() default "";

    Class<?> fallback() default void.class;

    boolean primary() default true;
}
