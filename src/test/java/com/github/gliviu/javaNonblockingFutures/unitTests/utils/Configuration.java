package com.github.gliviu.javaNonblockingFutures.unitTests.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Repeatable(Configurations.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface Configuration {
    int repetitions();
    int threadPool();
    /**
     * Run all repetitions in parallel or sequential.
     */
    boolean sequential() default false;
    String sleep1() default "";
    String sleep2() default "";
    String sleep3() default "";
    String fail1() default "";
    String fail2() default "";
    String fail3() default "";
    String fail4() default "";
    String fail5() default "";
    String fail6() default "";
    String option1() default "";
    String option2() default "";
    String option3() default "";
    String option4() default "";
}

