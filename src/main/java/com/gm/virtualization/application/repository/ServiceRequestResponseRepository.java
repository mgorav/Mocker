package com.gm.virtualization.application.repository;

import com.gm.virtualization.application.model.ServiceRequestResponse;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRequestResponseRepository extends CrudRepository<ServiceRequestResponse, Long> {

    ServiceRequestResponse lookUpRequestResponse(String url, String method, String requestHashKey);

    ServiceRequestResponse lookUpTcpRequestResponse(String url, String requestHashKey);

    ServiceRequestResponse lookUpGroovyRequestResponse(String url, String method);

    List<ServiceRequestResponse> lookUpRequestResponse(String url, String method);

    ServiceRequestResponse findByUrl(String url);
}
