package com.gm.virtualization.api.service;


import com.gm.virtualization.api.VirtualizationServiceApi;
import com.gm.virtualization.application.model.ServiceRequestResponse;
import com.gm.virtualization.application.repository.ServiceRequestResponseRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service of {@link VirtualizationServiceApi}
 */
@Component
@ConfigurationProperties()
public class VirtualizationService {

    @Autowired
    private ServiceRequestResponseRepository serviceRequestResponseRepository;

    @Getter
    @Setter
    @Value("${mocker.target.service.http.location}")
    private String location;


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void store(ServiceRequestResponse requestResponse) {

        serviceRequestResponseRepository.save(requestResponse);
    }

    @Transactional(readOnly = true)
    public ServiceRequestResponse lookUpRequestResponse(String url, String method, String requestHashKey) {

        return serviceRequestResponseRepository.lookUpRequestResponse(url, method, requestHashKey);
    }

    @Transactional(readOnly = true)
    public ServiceRequestResponse lookUpGroovyRequestResponse(String url, String method) {
        return serviceRequestResponseRepository.lookUpGroovyRequestResponse(url, method);
    }

    @Transactional(readOnly = true)
    public ServiceRequestResponse findByUrl(String url) {
        return serviceRequestResponseRepository.findByUrl(url);
    }

    @Transactional(readOnly = true)
    public ServiceRequestResponse lookUpTcpRequestResponse(String url, String requestHashKey) {
        return serviceRequestResponseRepository.lookUpTcpRequestResponse(url, requestHashKey);

    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void delete(String url, String httpMethod) {

        serviceRequestResponseRepository.delete(serviceRequestResponseRepository.lookUpRequestResponse(url, httpMethod));

    }

}
