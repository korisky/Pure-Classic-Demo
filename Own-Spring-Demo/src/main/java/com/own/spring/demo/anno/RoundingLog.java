package com.own.spring.demo.anno;

import java.lang.annotation.*;

/**
 * Rounding Log with JSONString output
 *
 * @author Roylic
 * 2023/1/12
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RoundingLog {
}
