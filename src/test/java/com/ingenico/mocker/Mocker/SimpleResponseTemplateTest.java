package com.ingenico.mocker.Mocker;

import com.gm.virtualization.application.model.ServiceRequestResponse;
import com.gm.virtualization.templating.groovy.SimpleResponseTemplate;
import com.gm.virtualization.templating.groovy.compilation.ResponseTemplateGroovySupportService;
import groovy.json.JsonSlurper;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class SimpleResponseTemplateTest {


    @Test
    public void testResponseTemplating() {

        String d = "import com.gm.virtualization.templating.groovy.SimpleResponseTemplate\n" +
                "import groovy.json.JsonOutput\n" +
                "import groovy.json.JsonSlurper\n" +
                "public class SimpleResponseTemplateImpl implements SimpleResponseTemplate {\n" +
                "    static AtomicInteger cnt = new AtomicInteger() \n" +
                "    public String template(String incomingRequest) {\n" +
                "        def jsonSlurper = new JsonSlurper()\n" +
                "        def object = jsonSlurper.parseText(incomingRequest)\n" +
                "        object.city = \"420\"\n" +
                "        object.description = \"420\"\n" +
                "        object.name = \"420\"\n" +
                "        object.id = cnt.incrementAndGet() \n" +
                "        return JsonOutput.toJson(object);\n" +
                "    }\n" +
                "} ";

        String script = "package com.gm.mocker.Mocker\n" +
                "\n" +
                "import com.gm.virtualization.templating.groovy.SimpleResponseTemplate\n" +
                "import groovy.json.JsonOutput\n" +
                "import groovy.json.JsonSlurper\n" +
                "\n" +
                "public class SimpleResponseTemplateImpl implements SimpleResponseTemplate {\n" +
                "    public String template(String incomingRequest) {\n" +
                "        def jsonSlurper = new JsonSlurper()\n" +
                "        def object = jsonSlurper.parseText(incomingRequest)\n" +
                "        object.simple = \"420\"\n" +
                "        return JsonOutput.toJson(object)\n" +
                "    }\n" +
                "}";


        System.out.println("Templating Script" + script);

        ServiceRequestResponse requestResponse = new ServiceRequestResponse();
        requestResponse.setGroovyTemplate(script.toString());

        SimpleResponseTemplate simpleResponseTemplate = (SimpleResponseTemplate) new ResponseTemplateGroovySupportService<>().parse(requestResponse);

        String incomingRequest = "{ \"simple\": 123,\n" +
                "      \"fraction\": 123.66,\n" +
                "      \"exponential\": 123e12\n" +
                "    }";


        JsonSlurper jsonSlurper = new JsonSlurper();
        Map<String, String> map = (Map<String, String>) jsonSlurper.parseText(simpleResponseTemplate.template(incomingRequest));

        assertEquals("420", map.get("simple"));

    }
}
