package com.lioncorp.ratelimiter.aop;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;

@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = {ElementType.TYPE})
@Documented
@Import(value = RateLimiterRegistrar.class)
public @interface EnableRateLimiter {
    String[] value() default {"com.netease.ai.rec.ratelimiter.service.strategy"};
    int order() default Ordered.LOWEST_PRECEDENCE;
}
