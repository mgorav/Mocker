package com.gm.virtualization.application.configurer;

import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static springfox.documentation.spi.DocumentationType.SWAGGER_2;

/**
 * This class integrates Swagger/Open API specification in IIN Service
 */
@Configuration
@EnableSwagger2
@ComponentScan("com.ingenico.virtualization.api")
public class VirtualizationSwaggerConfigurer {

    @Bean
    public Docket api() {
        return new Docket(SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(Predicates.not(PathSelectors.regex("/error")))
                .build()
                .pathMapping("/")
                .apiInfo(apiInfo());
    }

    @Bean
    public UiConfiguration uiConfig() {
        return new UiConfiguration("validatorUrl");
    }


    private ApiInfo apiInfo() {
        String description = "application Visualization Service";
        return new ApiInfoBuilder()
                .title("application")
                .description(description)
                .license("Ingenico")
                .licenseUrl("https://www.ingenico.com/")
                .version("0.1")
                .build();
    }
}
