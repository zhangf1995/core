package zf.core.sentinel.fusing;

import javax.validation.constraints.NotNull;
import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FusingResource {
    @NotNull
    String value() default "";
    @NotNull
    Class<?>  fallback();
}
