package com.gm.virtualization.application.configurer;

import com.gm.virtualization.api.ResponseEntityCreator;
import com.gm.virtualization.api.service.VirtualizationService;
import com.gm.virtualization.application.model.ServiceRequestResponse;
import com.gm.virtualization.cli.VirtualizationServiceCli;
import com.gm.virtualization.templating.groovy.compilation.ResponseTemplateGroovySupportService;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;

import static java.util.Arrays.asList;

/**
 * Configures {@link VirtualizationService} and its dependencies
 */
@Configuration
@EntityScan(basePackageClasses = ServiceRequestResponse.class)
public class VirtualizationServiceConfiguration {


    @Bean
    public VirtualizationService mockerService() {
        return new VirtualizationService();
    }

    @Bean
    public ResponseEntityCreator responseEntityCreator() {

        return new ResponseEntityCreator();
    }

    @Bean
    public RestTemplate restTemplate() {

        RestTemplate restTemplate = new RestTemplate(getClientHttpRequestFactory());
        restTemplate.setMessageConverters(asList(new StringHttpMessageConverter()));
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler());

        return restTemplate;
    }

    @Bean
    public VirtualizationServiceCli virtualizationServiceCli() {

        return new VirtualizationServiceCli();
    }

    @Bean
    public ResponseTemplateGroovySupportService responseTemplateGroovyCompilationService() {

        return new ResponseTemplateGroovySupportService();
    }


    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.dataSource")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "dataSource")
    @Primary
    public DataSource dataSource() {
        return dataSourceProperties().initializeDataSourceBuilder().build();
    }

    private ClientHttpRequestFactory getClientHttpRequestFactory() {
        int timeout = 5000;
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setConnectTimeout(timeout);
        return clientHttpRequestFactory;
    }
}
