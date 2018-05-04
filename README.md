**Mocker**
A Mocker is a Spring Boot based service. This service provide abilities to virtualize (mock) another webservice 
(SOAP/REST) or TCP based communication. It also provides ability to record & play scenario and hence provide ability to virutalize a service if it's not even built.

When a call to actual service is made via **Mocker**, this service first time goes to the actual service and all
subsequent calls will be addressed by **Mocker**. This is also referred to as recording of a call. As a result, **Mocker** can
totally replace an actual service once the recording is complete.

**Mocker** also support response templating/plugin custom functionality using Groovy along with ability to record scenario. This will explained in detail in section "Mocker Operations"

**NOTE** Mocker use hash key lookup on: URL + HttpMethod + Request to uniquely identify cached response.

**NOTE**  Mocker uses [jpa-eclipse](https://github.com/mgorav/jpa-eclipselink) to configure JPA

**What is Service Virutalization?**

[In software engineering, service virtualization is a method to emulate the behavior of specific components in 
heterogeneous component-based applications such as API-driven applications, cloud-based applications and 
service-oriented architectures. It is used to provide software development and QA/testing teams access to dependent 
system components that are needed to exercise an application under test (AUT), but are unavailable or 
difficult-to-access for development and testing purposes. With the behavior of the dependent components "virtualized", 
testing and development can proceed without accessing the actual live components. Service virtualization is recognized 
by vendors, industry analysts, and industry publications as being different than mocking.](https://en.wikipedia.org/wiki/Service_virtualization)



**Prerequisites:**
1. JDK 8
2. Maven 3.5.0
3. Lombok (need to be configured in IDE of the choice)
4. [jpa-eclipselink spring boot auto config library](https://github.com/mgorav/jpa-eclipselink)

----------

**Build and run Mocker**
1. Build >> mvn clean install
2. Run   >> java -jar Mocker-[version].jar

----------

**Mocker Operations**
Mocker is fully integrated with **Swagger**. Once **Mocker** starts successfully use following URL to see all the
available operations:

http://[hostName]:[portNumber]/swagger-ui.html

Following operations are supported by **Mocker**
1 Configure **target*. By "target" in *Mocker** means the actual location of the service being virtualized/mocked
  ````java
curl -X POST --header 'Content-Type: application/json' --header 'Accept: application/text' 'http://[hostName]:[portNumber]/mocker/change/target?url=http%3A%2F%2Flocalhost%3A8080%2Fws'
  ````
Once the target is configured the consumer has to send all the request to **Mocker** service i.e.
  ````java
http:://[hostName]:[portNumber]/mocker
  ````
2  **View Target**
  ````java
curl -X GET --header 'Accept: application/json' 'http://[hostName]:[target]/mocker/view/target'
  ````
  
3 **Add a scenario**  
  ````java
curl -X POST --header 'Content-Type: application/json' --header 'Accept: text/plain' 'http://[hostName]:[portNumber]/mocker/add/scenario?url=http%3A%2F%2Flocalhost%3A8080%2Fws&httpMethod=POST&status=200&httpHeaders=Accept%3Dapplication%2Fjson%2CContent-Type%3Dapplication%2Fjson&request=%7B%20%22attr%22%3A%22value%22%7D&response=%7B%20%22status%22%3A%22sucess%22%7D'
  ````
  
**NOTE**  
 Mocker also provides templating feature i.e. if groovy script is configured, Mocker will use to create the response. 
 The Mocker will pass request payload (if any) to groovy script. The groovy script should implement following 
 interfaces:
 
 ```` java
 public interface SimpleResponseTemplate {
 
       String template(String incomingRequest);
 }
 ````
 
  ```` java
import org.springframework.http.HttpHeaders;

public interface SmartResponseTemplate extends ResponseTemplate {

    String template(String incomingRequest, String url, String httpMethod, HttpHeaders httpHeaders);
}

  ````


An example of SimpleResponseTemplate in groovy:
 ```` java
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
 ````
 
 Using following **Add scenario** one can configure templating feature of Mocker:
 
  ````java
  curl -X POST --header 'Content-Type: application/json' --header 'Accept: text/plain' -d '{ \ 
     "url": "http://localhost:5000/example/v1/hotels/", \ 
     "httpMethod": "POST", \ 
     "status": "200", \ 
     "groovyTemplate": "import com.gm.virtualization.templating.groovy.SimpleResponseTemplate\nimport groovy.json.JsonOutput\nimport groovy.json.JsonSlurper\n\npublic class SimpleResponseTemplateImpl implements SimpleResponseTemplate{\n    public String template(String incomingRequest) {\n\n        def jsonSlurper = new JsonSlurper()\n\n        def object = jsonSlurper.parseText(incomingRequest)\n\n        object.city = \"420\"\n        object.description = \"420\"\n        object.name = \"420\"\n\n        return JsonOutput.toJson(object) \n    }\n} " \ 
   }' 'http://[hostname]:[portNumber]/mocker/add/scenario'
   ````
   
As a result of adding above, all the calls to url "http://localhost:5000/example/v1/hotels/" with method "POST" will
be intercepted and configured groovy script will be called to construct response. It's worth mentioning that Mocker 
will pass incoming request payload to configured ResponseTemplate aware groovy script (as shown above)
 




4  **Update a Scenario**  

  ````java
curl -X PUT --header 'Content-Type: application/json' --header 'Accept: application/json' 'http://[hostName]:[portNumber]/mocker/update/scenario?status=201'
 ````
5  **Delete a Scenario**  

  ````java
curl -X DELETE --header 'Accept: application/json' 'http://localhost:8090/mocker/delete/scenario?url=http%3A%2F%2Flocalhost%3A8080%2Fws&httpMethod=POST'
  ````

**Configurable Mocker Properties - application.yml**  
Below are the example of some of the configurable properties of **Mocker**:

1 Spring boot http port
  
    server:port:8090  
   
2 management port
    management.port 8091
  
3. tomcat (spring boot) threads
    ```` yaml
    server:  
        tomcat:  
            max-threads:20
    ````
    
4. Mocker properties  
 ```` yaml
mocker:
   tcp:
    server:
      port: "7777"
   target:
     service:
      #location: "http://localhost:8080/ws"
      #location: "http://samples.openweathermap.org"
      http:
          location: "http://localhost:5000/example/v1/hotels"
      tcp:
          location:
            hostname: "localhost"
            tcpport: 5555
 ````
 






"# Mocker" 
