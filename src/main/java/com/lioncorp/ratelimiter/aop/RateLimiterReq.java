package com.lioncorp.ratelimiter.aop;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimiterReq {
    
    String key();
    
    int qps() default 0;
    
    RateLimiterStrategy strategy() default RateLimiterStrategy.DEF_RATELIMITER;
}
