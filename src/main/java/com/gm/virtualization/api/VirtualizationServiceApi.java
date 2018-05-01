package com.gm.virtualization.api;


import com.gm.virtualization.api.service.VirtualizationService;
import com.gm.virtualization.application.model.ServiceRequestResponse;
import com.gm.virtualization.application.repository.ServiceRequestResponseRepository;
import com.gm.virtualization.templating.groovy.ResponseTemplate;
import com.gm.virtualization.templating.groovy.SimpleResponseTemplate;
import com.gm.virtualization.templating.groovy.SmartResponseTemplate;
import com.gm.virtualization.templating.groovy.compilation.ResponseTemplateGroovySupportService;
import com.gm.virtualization.util.HashKeyUtil;
import com.gm.virtualization.util.UrlUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.util.StringUtils.isEmpty;
import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * Virtual Service APIs
 */
@RestController
@RequestMapping(value = "/mocker")
@Api(tags = {"Virtual Service"})
@Slf4j
public class VirtualizationServiceApi<T extends ResponseTemplate> {

    @Autowired
    private VirtualizationService virtualizationService;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ResponseEntityCreator responseEntityCreator;
    @Autowired
    private ResponseTemplateGroovySupportService compilationService;
    @Autowired
    private ServiceRequestResponseRepository repository;


    @RequestMapping(value = "/ping",
            method = GET,
            produces = {"application/text"})
    @ResponseStatus(OK)
    @ApiOperation(value = "PING", notes = "PING")
    public @ResponseBody
    String ping() {


        return "Alive & Kicking";
    }

    /**
     * A controller method which checks is response is available and if not goes to actual service and store the
     * response
     * This method will not visible in Swagger UI
     *
     * @param httpRequest
     * @param httpResponse
     * @param incomingHttpEntity
     * @throws IOException
     * @throws ServletException
     */
    @RequestMapping("/**")
    @ApiOperation(value = "", hidden = true)
    public void recordAndAfterwardRespondFromCache(HttpServletRequest httpRequest, HttpServletResponse httpResponse, HttpEntity<String> incomingHttpEntity) throws IOException, ServletException {

        String uri = httpRequest.getRequestURI();
        HttpHeaders httpHeaders = incomingHttpEntity.getHeaders();
        String headersString = httpHeaders.toString();

        String redirectUrl = virtualizationService.getLocation() + uri.replaceAll("/mocker", "");
        String queryString = httpRequest.getQueryString();
        if (!isEmpty(queryString)) {
            redirectUrl = redirectUrl + "?" + queryString;
        }

        redirectUrl = UrlUtil.getCorrectUrl(redirectUrl);

        String incomingRequestPayload = incomingHttpEntity.getBody();
        String requestHashKey = HashKeyUtil.getHashKey(incomingRequestPayload);

        ServiceRequestResponse serviceRequestResponse = virtualizationService.lookUpRequestResponse(redirectUrl, httpRequest.getMethod(), requestHashKey);


        // Check it request is configured for templating
        if (serviceRequestResponse == null) {
            ServiceRequestResponse templatingRequestResponse = virtualizationService.lookUpGroovyRequestResponse(UrlUtil.getCorrectUrl(redirectUrl), httpRequest.getMethod());

            if (templatingRequestResponse != null) {
                serviceRequestResponse = templatingRequestResponse;
            }
        }


        if (serviceRequestResponse == null) {

            HttpEntity<String> newRequestEntity = new HttpEntity<>(incomingRequestPayload, getNewRequestHttpHeaders(httpHeaders));

            ResponseEntity<String> responseEntity = getResponseEntity(httpRequest, redirectUrl, newRequestEntity);

            httpResponse.setStatus(responseEntity.getStatusCodeValue());

            addResponseHttpHeaders(httpResponse, responseEntity);

            addBodyToResponse(httpResponse, responseEntity.getBody());

            virtualizationService.store(newServiceResponse(httpRequest, headersString, redirectUrl, incomingRequestPayload, requestHashKey, responseEntity.getBody(), responseEntity.getStatusCodeValue()));

        } else {
            String response = serviceRequestResponse.getResponse();

            if (!isEmpty(serviceRequestResponse.getGroovyHashKey())) {
                // Use groovy template to get the response
                ResponseTemplate responseTemplate = compilationService.getResponseTemplate(serviceRequestResponse.getGroovyHashKey());
                if (responseTemplate instanceof SimpleResponseTemplate) {
                    response = ((SimpleResponseTemplate) responseTemplate).template(incomingRequestPayload);
                } else {
                    response = ((SmartResponseTemplate) responseTemplate).template(incomingRequestPayload, redirectUrl, httpRequest.getMethod(), httpHeaders);
                }
            }

            addBodyToResponse(httpResponse, response);
            httpResponse.setStatus(Integer.valueOf(serviceRequestResponse.getStatus()));
        }
    }

    /**
     * Sets the target URL for {@link VirtualizationServiceApi}
     *
     * @param target
     * @return
     */
    @PostMapping(value = "/change/target",
            consumes = {"application/json"},
            produces = {"application/text"}
    )
    @ResponseStatus(OK)
    public @ResponseBody
    String setTargetUrl(Target target) {

        synchronized (this) {
            virtualizationService.setLocation(target.getUrl());
        }

        return "Mocking target changed";
    }

    /**
     * Get currently active target
     *
     * @return
     */
    @RequestMapping(value = "view/target",
            method = GET,
            produces = {"application/json"}
    )
    @ResponseStatus(OK)
    public @ResponseBody
    Target getTarget() {

        Target target = new Target(virtualizationService.getLocation());

        return target;
    }

    /**
     * Add a request/response scenario
     *
     * @param serviceRequestResponse
     * @return
     */
    @RequestMapping(value = "/add/scenario",
            method = POST
    )
    @ResponseStatus(OK)
    public @ResponseBody
    String addScenario(@RequestBody ServiceRequestResponse serviceRequestResponse) {

        serviceRequestResponse.doCorrectUrl();

        serviceRequestResponse.setRequestHashKey(HashKeyUtil.getHashKey(serviceRequestResponse.getRequest()));

        String groovyHashKey = getGroovyTemplateHashKey(serviceRequestResponse);

//        if (virtualizationService.lookUpRequestResponse(serviceRequestResponse.getUrl(), serviceRequestResponse.getHttpMethod(), serviceRequestResponse.getRequestHashKey()) != null) {
//            return "Scenario already exists";
//        }

        if (!isEmpty(groovyHashKey)) {

            ResponseTemplate simpleResponseTemplate = compilationService.parse(serviceRequestResponse);

            compilationService.addResponseTemplate(groovyHashKey, (T) simpleResponseTemplate);
        }

        virtualizationService.store(serviceRequestResponse);

        return "Successfully added scenario";
    }

    /**
     * Updates an existing recorded request/response
     *
     * @param serviceRequestResponse
     * @return
     */
    @RequestMapping(value = "/update/scenario",
            method = PUT
    )
    @ResponseStatus(OK)
    public @ResponseBody
    String updateScenario(@RequestBody ServiceRequestResponse serviceRequestResponse) {
        serviceRequestResponse.doCorrectUrl();
        String url = serviceRequestResponse.getUrl();
        String method = serviceRequestResponse.getHttpMethod();

        ServiceRequestResponse existRequestResponse = virtualizationService.lookUpGroovyRequestResponse(url, method);

        if (serviceRequestResponse != null) {
            throw new RuntimeException(format("Already exist recording for url %s & method %s for templating", url, method));
        }


        serviceRequestResponse.setRequestHashKey(HashKeyUtil.getHashKey(serviceRequestResponse.getRequest()));

        String groovyHashKey = getGroovyTemplateHashKey(serviceRequestResponse);

        if (!isEmpty(groovyHashKey)) {

            ResponseTemplate simpleResponseTemplate = compilationService.parse(serviceRequestResponse);

            compilationService.addResponseTemplate(groovyHashKey, (T) simpleResponseTemplate);
        }


        virtualizationService.store(serviceRequestResponse);

        return "Successfully updated scenario";
    }

    /**
     * Delete's a scenario(s)
     *
     * @param url
     * @param httpMethod
     * @return
     */
    @RequestMapping(value = "/delete/scenario",
            method = DELETE
    )
    @ResponseStatus(OK)
    public @ResponseBody
    String deleteScenario(String url, String httpMethod) {

        virtualizationService.delete(url, httpMethod);

        return "Successfully deleted scenario";
    }

    /**
     * Get all the request/responses for a given URL
     *
     * @return
     */
    @RequestMapping(value = "view/scenario/",
            method = GET,
            produces = {"application/json"})
    @ResponseStatus(OK)
    public @ResponseBody
    ServiceRequestResponse viewScenario(@RequestParam("url") String url) {


        return virtualizationService.findByUrl(UrlUtil.getCorrectUrl(url));
    }

    // ~~~ protected/private utility methods abstraction
    protected ResponseEntity<String> getResponseEntity(HttpServletRequest httpRequest, String redirectUrl, HttpEntity<String> entity) {


        return responseEntityCreator.getResponseEntity(httpRequest.getMethod(), redirectUrl, entity);
    }

    protected ServiceRequestResponse newServiceResponse(HttpServletRequest httpRequest, String headersString, String redirectUrl, String request, String requestHashKey, String result, int status) {
        ServiceRequestResponse serviceRequestResponse;
        serviceRequestResponse = new ServiceRequestResponse();
        serviceRequestResponse.setUrl(redirectUrl);
        serviceRequestResponse.setStatus("" + status);
        serviceRequestResponse.setHttpHeaders(headersString);
        serviceRequestResponse.setHttpMethod(httpRequest.getMethod());
        serviceRequestResponse.setResponse(result);
        serviceRequestResponse.setRequest(request);
        serviceRequestResponse.setRequestHashKey(requestHashKey);
        return serviceRequestResponse;
    }


    protected void addBodyToResponse(HttpServletResponse response, String body) throws IOException {
        response.getWriter().write(body);
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected HttpHeaders getNewRequestHttpHeaders(HttpHeaders httpHeaders) {
        HttpHeaders newHttpHeaders = new HttpHeaders();
        httpHeaders.entrySet().forEach(entrySet -> {
            if (!entrySet.getKey().equalsIgnoreCase("host")) {
                entrySet.getValue().forEach(value -> {
                    newHttpHeaders.add(entrySet.getKey(), value);
                });
            }
        });

        return httpHeaders;
    }

    protected void addResponseHttpHeaders(HttpServletResponse httpResponse, ResponseEntity<String> responseEntity) {
        responseEntity.getHeaders().entrySet().forEach(entrySet -> {
            List<String> values = entrySet.getValue();
            values.forEach(value -> {
                httpResponse.addHeader(entrySet.getKey(), value);
            });
        });
    }

    private String getGroovyTemplateHashKey(ServiceRequestResponse serviceRequestResponse) {
        String groovyTemplate = serviceRequestResponse.getGroovyTemplate();
        // url + method + groovy script uniquely identifies hashKey
        String groovyHashKey = HashKeyUtil.getHashKey(serviceRequestResponse.getUrl() + "#" + serviceRequestResponse.getHttpMethod() + "#" + groovyTemplate);
        serviceRequestResponse.setGroovyHashKey(groovyHashKey);
        return groovyHashKey;
    }

    /**
     * This class is an abstraction of URL
     */
    @Setter
    @Getter
    @ToString
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    static final class Target {
        String url;
    }
}
