package zf.core.sentinel.fusing;

import org.springframework.context.annotation.Import;
import zf.core.sentinel.fusing.register.FusingBeaDefitionRegister;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(FusingBeaDefitionRegister.class)
public @interface EnableFusing {

    boolean openFusing() default false;
}
