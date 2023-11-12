package com.bring.postprocessor;

import com.bring.domain.BeanDefinition;
import com.bring.domain.BeanTypeEnum;
import com.bring.support.DefaultBeanFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ConfigurationClassPostProcessor implements BeanPostProcessor {
    
    @Override
    public void postProcessBeanFactory(DefaultBeanFactory defaultBeanFactory) {
        Map<String, BeanDefinition> beanDefinitionMap = defaultBeanFactory.getBeanDefinitionMap();
        
        defaultBeanFactory.getBeanDefinitionNames()
                .forEach(beanName -> {
                    BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
                    
                    if (beanDefinition.getBeanType() == BeanTypeEnum.CONFIGURATION) {
                      for (Method method : beanDefinition.getBeanClass().getDeclaredMethods()) {
                        loadBeanDefinitionsForBeanMethod(defaultBeanFactory, beanName, method);
                      }
                    }
                });
    }

      private void loadBeanDefinitionsForBeanMethod(DefaultBeanFactory defaultBeanFactory, String beanName, 
                                                    Method method) {
        BeanDefinition beanDefinition = BeanDefinition.builder()
            .beanClass(method.getReturnType())
            .beanType(BeanTypeEnum.findBeanType(method))
            .isSingleton(true)
            .method(method)
            .factoryMethodName(method.getName())
            .factoryBeanName(beanName)
            .build();
        
          defaultBeanFactory.registerBeanDefinition(beanDefinition);
      }

}
