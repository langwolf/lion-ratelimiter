package com.lioncorp.ratelimiter.aop;

import java.util.Objects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import com.lioncorp.ratelimiter.service.strategy.IRateLimiterStrategy;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Configuration
@Order(0)
@Slf4j
public class RateLimiterAspect {

    @Qualifier("defRateLimiterStrategy")
    @Autowired(required = false)
    private IRateLimiterStrategy defRateLimiterStrategy;
    
    @Around("@annotation(rateLimiterReq)")
    public Object doAroundAdvice(ProceedingJoinPoint pjp, RateLimiterReq rateLimiterReq) throws Throwable {
        if(Objects.isNull(defRateLimiterStrategy)) {
            log.error("rateLimiter strategy bean not be injected");
            return pjp.proceed();
        }
        return defRateLimiterStrategy.strategy(pjp, rateLimiterReq);
    }
}
