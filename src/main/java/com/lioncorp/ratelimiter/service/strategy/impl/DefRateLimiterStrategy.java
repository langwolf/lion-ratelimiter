package com.lioncorp.ratelimiter.service.strategy.impl;

import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.RateLimiter;
import com.lioncorp.ratelimiter.aop.RateLimiterReq;
import com.lioncorp.ratelimiter.configuration.consul.DynamicProperties;
import com.lioncorp.ratelimiter.service.strategy.IRateLimiterStrategy;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefRateLimiterStrategy implements IRateLimiterStrategy {

    private Map<String, RateLimiter> limiters = Maps.newConcurrentMap();
    
    @Autowired(required = false)
    private DynamicProperties dynamicProperties;
    
    private final static int DEF_QPS = 3000;
    private final static String DEF_PROJECT = "def";
    
    @Override
    public Object strategy(ProceedingJoinPoint pjp, RateLimiterReq rateLimiterReq) throws Throwable {

        int qps = rateLimiterReq.qps();
        String method = rateLimiterReq.key(); 
        Object[] param = pjp.getArgs();
        if(ArrayUtils.isEmpty(param)) {
            return pjp.proceed();
        }
        String project = "";
        if (ArrayUtils.isNotEmpty(param)) {
            JSONObject obj = (JSONObject)JSONObject.toJSON(param[0]);
            project = MapUtils.getString(obj, "project", "");
            String item = MapUtils.getString(obj, "item", "");
            if(StringUtils.isNotEmpty(item)) {
                project = item;
            }       
        }
        if(StringUtils.isEmpty(project)){
            project = DEF_PROJECT;
        }
        String key = String.join("_", project, method);
        if(Objects.nonNull(dynamicProperties)
                && MapUtils.isNotEmpty(dynamicProperties.getQps())) {
            qps =  dynamicProperties.getQps().getOrDefault(key, qps);
        }    
        qps = (qps == 0)? DEF_QPS : qps;
        RateLimiter rateLimiter = limiters.get(key);
        if (rateLimiter != null) {
            rateLimiter.setRate(qps);          
        } else {
            RateLimiter tmp = RateLimiter.create(qps);
            rateLimiter = tmp;
            RateLimiter rateLimiterOld = limiters.putIfAbsent(key, tmp);
            if (rateLimiterOld != null) {
                rateLimiter = rateLimiterOld;
            }
        }
        
        if (rateLimiter == null) {
            return pjp.proceed();
        }
        if(rateLimiter.tryAcquire()) {
            return pjp.proceed();
        }
        log.info("rateLimited, key:{}", key);
        return "{\"status\":1,\"message\":\"no data, rateLimited\",\"data\":null}";
    }
    /**
    public static void main(String[] args) throws Exception {
        BloomFilter<String> filter = BloomFilter.create((from, into) -> {
                into.putString(from, Charsets.UTF_8);
        }, 500000,0.0000001);
        for (int index = 0; index < 500000; index++) {
            filter.put("abc_test_" + index);
        }
        System.out.println("write all...");
        for (int i = 500000; i < 1000000; i++) {
            if (filter.mightContain("abc_test_" + i)) {
                System.out.println("yes");
            }
        }
    }
    **/
}
