package dev.galactic.star.database.impl.mapping.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares whether the field is a parameter.
 * @author Verox001
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DatabaseField {
    /**
     * the name of the database field.
     * @return String.
     */
    String name() default "";

    boolean pk() default false;

    /**
     * Whether values can be null.
     * @return Boolean.
     */
    boolean canBeNull() default false;

    /**
     * Whether the values should be unique.
     * @return Boolean.
     */
    boolean unique() default false;
}
