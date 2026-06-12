package co.ke.xently.log.mask;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE, ElementType.RECORD_COMPONENT, ElementType.METHOD})
public @interface NoLogForging {
}
