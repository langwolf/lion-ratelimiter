package com.lioncorp.ratelimiter.aop;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;

import com.lioncorp.ratelimiter.service.strategy.IRateLimiterStrategy;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RateLimiterRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(annotationMetadata.getAnnotationAttributes(EnableRateLimiter.class.getName()));
        String[] basePackages = (String[]) attributes.getStringArray("value");
        if(org.apache.commons.lang3.ArrayUtils.isEmpty(basePackages)) {
            log.error("no rateLimiter");
            return;
        }
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(registry, false);
        TypeFilter rateLimiterStrategyFilter = new AssignableTypeFilter(IRateLimiterStrategy.class);
        scanner.addIncludeFilter(rateLimiterStrategyFilter);
        scanner.scan(basePackages);
        for (String basePackage : basePackages) {
            scanner.findCandidateComponents(basePackage).forEach(beanDefinition -> {                
                registry.registerBeanDefinition(beanDefinition.getBeanClassName(), beanDefinition);
                log.info("{} is registered", beanDefinition.getBeanClassName());
            });
        }
    }
}
