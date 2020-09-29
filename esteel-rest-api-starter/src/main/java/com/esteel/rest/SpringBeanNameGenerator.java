package com.esteel.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.util.StringUtils;

@Slf4j
public class SpringBeanNameGenerator extends AnnotationBeanNameGenerator {
  @Override
  protected String buildDefaultBeanName(BeanDefinition definition) {
    String beanName ;
    if (definition instanceof AnnotatedBeanDefinition) {
      beanName = determineBeanNameFromAnnotation((AnnotatedBeanDefinition) definition);
      if (StringUtils.hasText(beanName)) {
        // Explicit bean name found.
        log.debug("buildDefaultBeanName:{}",beanName);
        return beanName;
      }
    }
    beanName = definition.getBeanClassName();
    log.debug("buildDefaultBeanName:{}",beanName);
    return beanName;
  }
}
