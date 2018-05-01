package com.gm.virtualization.api;

import com.gm.virtualization.util.HttpHeadersUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import static org.springframework.http.HttpMethod.*;

/**
 * This class is responsible for creating {@link ResponseEntity}
 */
@Component
public class ResponseEntityCreator {

    @Autowired
    private RestTemplate restTemplate;

    public ResponseEntity<String> getResponseEntity(String incomingRequest, String url, String httpMethod, HttpHeaders httpHeaders) {

        HttpEntity<String> newRequestEntity = new HttpEntity<>(incomingRequest, HttpHeadersUtil.getAptRequestHttpHeaders(httpHeaders));


        return getResponseEntity(httpMethod, url, newRequestEntity);
    }

    public ResponseEntity<String> getResponseEntity(String httpMethod, String redirectUrl, HttpEntity<String> entity) {
        ResponseEntity<String> result;
        switch (httpMethod.toLowerCase()) {
            case "get":
                result = restTemplate.exchange(redirectUrl, GET, entity, String.class);
                break;
            case "put":
                result = restTemplate.exchange(redirectUrl, PUT, entity, String.class);
                break;
            case "patch":
                result = restTemplate.exchange(redirectUrl, PATCH, entity, String.class);
                break;
            case "post":
                result = restTemplate.exchange(redirectUrl, POST, entity, String.class);
                break;
            case "delete":
                result = restTemplate.exchange(redirectUrl, DELETE, entity, String.class);
                break;
            case "options":
                result = restTemplate.exchange(redirectUrl, OPTIONS, entity, String.class);
                break;
            case "trace":
                result = restTemplate.exchange(redirectUrl, TRACE, entity, String.class);
                break;
            case "head":
                result = restTemplate.exchange(redirectUrl, HEAD, entity, String.class);
                break;

            default:
                throw new UnsupportedOperationException("Unsupported HTTP method " + httpMethod);
        }

        return result;
    }
}
