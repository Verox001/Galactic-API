package dev.galactic.star.database.impl.mapping.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DatabaseField {
    String name() default "";
    boolean pk() default false;
    boolean canBeNull() default false;
    boolean unique() default false;
}
