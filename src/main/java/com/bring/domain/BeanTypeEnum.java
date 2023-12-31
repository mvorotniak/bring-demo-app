package com.bring.domain;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.bring.annotation.Bean;
import com.bring.annotation.Configuration;
import com.bring.utils.Pair;
import lombok.Getter;

@Getter
public enum BeanTypeEnum {
    
    CONFIGURATION(1, Configuration.class),
    
    CONFIGURATION_BEAN(2, Bean.class);

    private final int order;
    
    private final List<Class<?>> annotationClasses;
    
    BeanTypeEnum(int order, Class<?>... annotationClasses) {
        this.order = order;
        this.annotationClasses = List.of(annotationClasses);
    }

    public static BeanTypeEnum findBeanType(Class<?> clazz) {
        Map<Class<?>, BeanTypeEnum> annotationToBeanType = getAnnotationToBeanType();
        
        return Arrays.stream(clazz.getAnnotations())
                .map(annotation -> annotationToBeanType.get(annotation.annotationType()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(String.format(
                    "Unable to create Bean of type=[%s]. Class is not annotated with %s", 
                    clazz.getSimpleName(),
                    Arrays.stream(values()).map(BeanTypeEnum::getAnnotationClasses).flatMap(Collection::stream).toList())));
    }

    public static BeanTypeEnum findBeanType(Method method) {
        Map<Class<?>, BeanTypeEnum> annotationToBeanType = getAnnotationToBeanType();

        return Arrays.stream(method.getAnnotations())
            .map(annotation -> annotationToBeanType.get(annotation.annotationType()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException(String.format(
                "Unable to create Bean of type=[%s], methodName=[%s]. Method is not annotated with %s",
                method.getReturnType(), 
                method.getName(),
                Arrays.stream(values()).map(BeanTypeEnum::getAnnotationClasses).flatMap(Collection::stream).toList())));
    }
    
    private static Map<Class<?>, BeanTypeEnum> getAnnotationToBeanType() {
        return Arrays.stream(values())
            .flatMap(beanType -> beanType.getAnnotationClasses()
                .stream()
                .map(annotation -> Pair.of(annotation, beanType)))
            .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
    }
    
}
