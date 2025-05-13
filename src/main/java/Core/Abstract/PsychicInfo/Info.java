package Core.Abstract.PsychicInfo;

/*
Author : NOMUSAMO
Lisence : NULL
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD}) // 메서드에도 적용 가능하도록 수정
public @interface Info {
}