package com.bring.support;

import java.lang.reflect.Method;

import com.bring.domain.BeanDefinition;
import com.bring.domain.BeanTypeEnum;
import lombok.SneakyThrows;

public class DefaultInstantiationStrategy implements InstantiationStrategy {

    @SneakyThrows
    @Override
    public Object instantiate(BeanDefinition beanDefinition) {
      return beanDefinition.getBeanType() == BeanTypeEnum.CONFIGURATION 
        ? beanDefinition.getBeanClass().getDeclaredConstructor().newInstance() 
        : null;
    }

    @SneakyThrows
    @Override
    public Object instantiate(BeanDefinition beanDefinition, Object factoryBean, Method method, Object... args) {
      return method.invoke(factoryBean, args);
    }

}
