package com.bring.support;

import java.lang.reflect.Method;

import com.bring.domain.BeanDefinition;

public interface InstantiationStrategy {

  Object instantiate(BeanDefinition beanDefinition);

  Object instantiate(BeanDefinition beanDefinition, Object factoryBean, Method method, Object... args);
  
}
