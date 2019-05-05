package com.lioncorp.ratelimiter.service.strategy;

import org.aspectj.lang.ProceedingJoinPoint;

import com.lioncorp.ratelimiter.aop.RateLimiterReq;

public interface IRateLimiterStrategy {

    Object strategy(ProceedingJoinPoint pjp, RateLimiterReq rateLimiter) throws Throwable;
}
