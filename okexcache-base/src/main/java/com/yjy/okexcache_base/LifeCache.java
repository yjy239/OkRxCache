package com.yjy.okexcache_base;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2018/04/23
 *     desc   : the Annoation for the Cache LifeCycle
 *
 *     version: 1.0
 * </pre>
 */
@Target(METHOD)
@Retention(RUNTIME)
public @interface LifeCache {
    long duaration();

    TimeUnit unit();

    boolean setFromNet() default false;
}
