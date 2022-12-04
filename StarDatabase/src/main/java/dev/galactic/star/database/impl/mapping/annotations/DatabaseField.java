package dev.galactic.star.database.impl.mapping.annotations;

import dev.galactic.star.database.impl.objects.ColumnType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DatabaseField {
    String name() default "";
    boolean autoIncrements() default false;
    boolean canBeNull() default false;
    int maxSize();
    ColumnType fieldType();
}
