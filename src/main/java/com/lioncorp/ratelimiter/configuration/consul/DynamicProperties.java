package com.lioncorp.ratelimiter.configuration.consul;

import java.util.Map;
import java.util.Set;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import lombok.Getter;
import lombok.Setter;

@Configuration
@RefreshScope
@ConfigurationProperties("dynamic")
@Scope(value = "singleton")
public class DynamicProperties {

    @Setter
    @Getter
    private Map<String, Integer> qps;
    
    @Setter
    @Getter
    private Set<String> items;
}
