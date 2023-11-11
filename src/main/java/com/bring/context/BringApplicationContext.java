package com.bring.context;

public interface BringApplicationContext {
    
    void refresh();
    
    <T> T getBean(Class<T> clazz);
    
}
