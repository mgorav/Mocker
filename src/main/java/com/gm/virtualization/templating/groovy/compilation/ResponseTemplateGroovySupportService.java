package com.gm.virtualization.templating.groovy.compilation;

import com.gm.virtualization.application.model.ServiceRequestResponse;
import com.gm.virtualization.templating.groovy.ResponseTemplate;
import com.gm.virtualization.templating.groovy.SimpleResponseTemplate;
import groovy.lang.GroovyClassLoader;
import org.springframework.beans.BeanUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Thread.currentThread;

public class ResponseTemplateGroovySupportService<T extends ResponseTemplate> {

    private Map<String, T> groovyCache = new ConcurrentHashMap<>(10000);

    public T parse(ServiceRequestResponse serviceRequestResponse) {

        final GroovyClassLoader classLoader = new GroovyClassLoader(currentThread().getContextClassLoader());
        //classLoader.addClasspath(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
        Class<SimpleResponseTemplate> responseTemplateClass = classLoader.parseClass(serviceRequestResponse.getGroovyTemplate());

        return BeanUtils.instantiate((Class<T>) responseTemplateClass);

    }

    public T getResponseTemplate(ServiceRequestResponse serviceRequestResponse) {
        return groovyCache.get(serviceRequestResponse.getGroovyHashKey());
    }

    public T getResponseTemplate(String groovyHashKey) {
        return groovyCache.get(groovyHashKey);
    }

    public void addResponseTemplate(String groovyHashKey, T responseTemplate) {
        groovyCache.put(groovyHashKey, responseTemplate);
    }
}
