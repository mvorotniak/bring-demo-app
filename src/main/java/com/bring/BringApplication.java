package com.bring;

import com.bring.context.AnnotationConfigApplicationContext;
import com.bring.context.BringApplicationContext;

public class BringApplication {
    
    private final Class<?> primarySource;
    
    public BringApplication(Class<?> primarySource) {
        this.primarySource = primarySource;
    }
    
    public BringApplicationContext run() {
        BringApplicationContext context = this.createAnnotationConfigApplicationContext(this.primarySource);
        context.refresh();
        
        return context;
    }

    private AnnotationConfigApplicationContext createAnnotationConfigApplicationContext(Class<?> source) {
        return new AnnotationConfigApplicationContext(source);
    }

}
