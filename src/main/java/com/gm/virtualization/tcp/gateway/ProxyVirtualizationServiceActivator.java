package com.gm.virtualization.tcp.gateway;

import com.gm.virtualization.api.service.VirtualizationService;
import com.gm.virtualization.application.model.ServiceRequestResponse;
import com.gm.virtualization.templating.groovy.ResponseTemplate;
import com.gm.virtualization.templating.groovy.SimpleResponseTemplate;
import com.gm.virtualization.templating.groovy.SmartResponseTemplate;
import com.gm.virtualization.templating.groovy.compilation.ResponseTemplateGroovySupportService;
import com.gm.virtualization.util.HashKeyUtil;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Transformer;

import static org.springframework.util.StringUtils.isEmpty;

@MessageEndpoint
@ConfigurationProperties()
public class ProxyVirtualizationServiceActivator {
    @Autowired
    VirtualizationGateway gateway;
    @Autowired
    VirtualizationService virtualizationService;
    @Autowired
    ResponseTemplateGroovySupportService<? extends ResponseTemplate> groovySupportService;

    @Getter
    @Setter
    @Value("${mocker.target.service.tcp.location.hostname}")
    private String hostname;

    @Getter
    @Setter
    @Value("${mocker.target.service.tcp.location.tcpport}")
    private int tcpport;

    @Transformer(inputChannel = "thisTcpServer", outputChannel = "proxyTxpTargetServer")
    public String convert(byte[] bytes) {
        return new String(bytes);
    }

    @ServiceActivator(inputChannel = "proxyTxpTargetServer")
    public String upCase(String request) {
        String requestHashKey = HashKeyUtil.getHashKey(request);
        String url = hostname + ":" + tcpport;
        ServiceRequestResponse serviceRequestResponse = virtualizationService.lookUpTcpRequestResponse(url, requestHashKey);

        if (serviceRequestResponse != null) {
            return serviceRequestResponse.getResponse();
        }

        serviceRequestResponse = virtualizationService.lookUpTcpRequestResponse(url, null);

        if (serviceRequestResponse != null && !isEmpty(serviceRequestResponse.getGroovyHashKey())) {
            // Use templating
            ResponseTemplate responseTemplate = groovySupportService.getResponseTemplate(serviceRequestResponse.getGroovyHashKey());
            if (responseTemplate instanceof SimpleResponseTemplate) {
                return ((SimpleResponseTemplate) responseTemplate).template(request);
            } else {
                return ((SmartResponseTemplate) responseTemplate).template(request, url, null, null);
            }
        }

        String response = gateway.viaTcp(request);
        serviceRequestResponse = new ServiceRequestResponse();
        serviceRequestResponse.setUrl(url);
        serviceRequestResponse.setProtcol("tcp");
        serviceRequestResponse.setRequestHashKey(requestHashKey);
        serviceRequestResponse.setResponse(response);
        virtualizationService.store(serviceRequestResponse);


        return response;

    }

    @Transformer(inputChannel = "resultToString")
    public String convertResult(byte[] bytes) {
        return new String(bytes);
    }

}
