package com.gm.virtualization.templating.groovy;

public interface SimpleResponseTemplate extends ResponseTemplate {

    String template(String incomingRequest);
}
