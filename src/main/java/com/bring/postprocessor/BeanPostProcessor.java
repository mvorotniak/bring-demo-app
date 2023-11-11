package com.bring.postprocessor;

import com.bring.support.DefaultBeanFactory;

public interface BeanPostProcessor {
    
    void postProcessBeanFactory(DefaultBeanFactory defaultBeanFactory);
    
}
