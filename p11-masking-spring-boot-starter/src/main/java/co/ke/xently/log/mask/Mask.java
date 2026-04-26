package co.ke.xently.log.mask;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT})
public @interface Mask {
    MaskingStyle style() default MaskingStyle.DEFAULT;

    String maskCharacter() default "";
}
