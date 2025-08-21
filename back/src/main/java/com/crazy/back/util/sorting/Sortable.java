package com.crazy.back.util.sorting;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Sortable {
    SortType type() default SortType.STRING;

    String customComparator() default "";
}
