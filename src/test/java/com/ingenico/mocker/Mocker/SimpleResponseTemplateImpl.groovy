package com.ingenico.mocker.Mocker

import com.gm.virtualization.templating.groovy.SimpleResponseTemplate
import groovy.json.JsonOutput
import groovy.json.JsonSlurper

public class SimpleResponseTemplateImpl implements SimpleResponseTemplate {
    public String template(String incomingRequest) {

        def jsonSlurper = new JsonSlurper()

        def object = jsonSlurper.parseText(incomingRequest)

        object.city = "420"
        object.description = "420"
        object.name = "420"

        return JsonOutput.toJson(object);
    }
}