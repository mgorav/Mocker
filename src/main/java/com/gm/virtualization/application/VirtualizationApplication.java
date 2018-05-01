package com.gm.virtualization.application;

import com.gm.virtualization.api.VirtualizationServiceApi;
import com.gm.virtualization.application.configurer.VirtualizationServiceConfiguration;
import com.gm.virtualization.application.configurer.VirtualizationSwaggerConfigurer;
import com.gm.virtualization.application.repository.ServiceRequestResponseRepository;
import com.gm.virtualization.tcp.gateway.VirtualizationTcpGatewayConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Starts the {@link VirtualizationServiceApi}
 */
@SpringBootApplication
@EnableConfigurationProperties
@EnableAutoConfiguration()
@EnableJpaRepositories(basePackageClasses = ServiceRequestResponseRepository.class)
@Import({VirtualizationSwaggerConfigurer.class, VirtualizationServiceConfiguration.class, VirtualizationTcpGatewayConfig.class})
public class VirtualizationApplication {

    public static void main(String[] args) {
        SpringApplication.run(VirtualizationApplication.class, args);
    }
}
