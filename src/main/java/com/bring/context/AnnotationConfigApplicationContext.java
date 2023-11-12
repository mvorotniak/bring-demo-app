package com.bring.context;

import java.util.List;
import java.util.Optional;

import com.bring.postprocessor.BeanPostProcessor;
import com.bring.postprocessor.ConfigurationClassPostProcessor;
import com.bring.domain.BeanDefinition;
import com.bring.domain.BeanTypeEnum;
import com.bring.support.DefaultBeanFactory;

public class AnnotationConfigApplicationContext implements BringApplicationContext {
    
    private final List<BeanPostProcessor> processors = List.of(new ConfigurationClassPostProcessor());
    
    private final DefaultBeanFactory defaultBeanFactory;
    
    public AnnotationConfigApplicationContext(Class<?> source) {
        this.defaultBeanFactory = new DefaultBeanFactory();
        register(source);
    }

    private void register(Class<?> clazz) {
        BeanDefinition beanDefinition = BeanDefinition.builder()
            .beanClass(clazz)
            .beanType(BeanTypeEnum.findBeanType(clazz))
            .isSingleton(true)
            .factoryBeanName(clazz.getSimpleName())
            .build();
        
        this.defaultBeanFactory.registerBeanDefinition(beanDefinition);
    }

    @Override
    public void refresh() {
        invokeBeanFactoryPostProcessors();
        instantiateBeans();
    }

    @Override
    public <T> T getBean(Class<T> clazz) {
        Object obj = Optional.ofNullable(this.defaultBeanFactory.getTypeToBeanName().get(clazz))
          .map(beanName -> this.defaultBeanFactory.getSingletonObjects().get(beanName))
          .orElseThrow(() -> 
            new RuntimeException(String.format("Bean of type=[%s] does not exist.", clazz.getSimpleName())));
        
        return (T) obj;
    }

    private void invokeBeanFactoryPostProcessors() {
        this.processors.forEach(processor -> processor.postProcessBeanFactory(this.defaultBeanFactory));
    }

    private void instantiateBeans() {
        this.defaultBeanFactory.instantiateSingletons();
    }
    
}
