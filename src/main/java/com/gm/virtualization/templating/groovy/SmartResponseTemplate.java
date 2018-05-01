package com.gm.virtualization.templating.groovy;

import org.springframework.http.HttpHeaders;

public interface SmartResponseTemplate extends ResponseTemplate {

    String template(String incomingRequest, String url, String httpMethod, HttpHeaders httpHeaders);
}
