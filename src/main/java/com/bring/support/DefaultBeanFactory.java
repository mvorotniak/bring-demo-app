package com.bring.support;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.bring.domain.BeanDefinition;
import com.bring.domain.BeanTypeEnum;
import com.thoughtworks.paranamer.AnnotationParanamer;
import com.thoughtworks.paranamer.BytecodeReadingParanamer;
import com.thoughtworks.paranamer.CachingParanamer;
import com.thoughtworks.paranamer.Paranamer;
import lombok.Getter;

@Getter
public class DefaultBeanFactory {
    
    private final Paranamer info = new CachingParanamer(new AnnotationParanamer(new BytecodeReadingParanamer()));

    private final InstantiationStrategy instantiationStrategy;
    
    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
    
    private final Map<Class<?>, String> typeToBeanName = new ConcurrentHashMap<>();
    
    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>();
    
    private final Set<String> beanDefinitionNames = new HashSet<>();

    public DefaultBeanFactory() {
        this.instantiationStrategy = new DefaultInstantiationStrategy();
    }
    
    public void registerBeanDefinition(BeanDefinition beanDefinition) {
        this.registerBeanDefinitions(Collections.singletonList(beanDefinition));
    }
    
    public void registerBeanDefinitions(List<BeanDefinition> beanDefinitions) {
        Map<String, BeanDefinition> definitions = beanDefinitions.stream()
                .collect(Collectors.toMap(this::generateBeanName, Function.identity()));
        
        Map<Class<?>, String> types = definitions.entrySet()
          .stream()
          .collect(Collectors.toMap(entry -> entry.getValue().getBeanClass(), Entry::getKey));
        
        this.beanDefinitionMap.putAll(definitions);
        this.beanDefinitionNames.addAll(beanDefinitionMap.keySet());
        this.typeToBeanName.putAll(types);
    }
    
    private String generateBeanName(BeanDefinition beanDefinition) {
        return beanDefinition.getBeanType() == BeanTypeEnum.CONFIGURATION_BEAN
          ? beanDefinition.getFactoryMethodName()
          : beanDefinition.getFactoryBeanName();
    }

    public void instantiateSingletons() {
        this.beanDefinitionMap.entrySet()
          .stream()
          .filter(entry -> entry.getValue().isSingleton())
          .sorted(Comparator.comparing(entry -> entry.getValue().getBeanType().getOrder()))
          .forEach(entry -> this.registerSingleton(entry.getKey(), entry.getValue()));
    }
    
    private Object registerSingleton(String beanName, BeanDefinition beanDefinition) {
        if (Objects.nonNull(this.singletonObjects.get(beanName))) {
            return this.singletonObjects.get(beanName);
        }
        
        Object obj;
        if (Objects.nonNull(beanDefinition.getMethod())) {
            Object configObj = this.singletonObjects.get(beanDefinition.getFactoryBeanName());
            List<String> methodParamNames = Arrays.stream(this.info.lookupParameterNames(beanDefinition.getMethod()))
              .toList();
            
            List<Object> methodObjs = new ArrayList<>();
            methodParamNames.forEach(paramName -> {
                Object o = this.singletonObjects.get(paramName);
                if (Objects.nonNull(o)) {
                    methodObjs.add(o);
                } else {
                    BeanDefinition bd = Optional.ofNullable(this.beanDefinitionMap.get(paramName))
                            .orElseThrow(() -> new RuntimeException(String.format(
                                    "Configuration method parameter injection: Unable to inject [%s] into [%s].", 
                                    paramName, beanDefinition.getFactoryMethodName())));
                    Object newObj = registerSingleton(bd.getFactoryMethodName(), bd);
                    methodObjs.add(newObj);
                }
            });
            
            obj = this.instantiationStrategy.instantiate(beanDefinition, configObj, beanDefinition.getMethod(), 
                    methodObjs.toArray());
        } else {
            obj = this.instantiationStrategy.instantiate(beanDefinition);
        }

        this.singletonObjects.put(beanName, obj);
        
        return obj;
    }
    
}
