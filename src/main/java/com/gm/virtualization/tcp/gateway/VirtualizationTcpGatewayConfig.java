package com.gm.virtualization.tcp.gateway;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.ip.tcp.TcpInboundGateway;
import org.springframework.integration.ip.tcp.TcpOutboundGateway;
import org.springframework.integration.ip.tcp.connection.AbstractClientConnectionFactory;
import org.springframework.integration.ip.tcp.connection.AbstractServerConnectionFactory;
import org.springframework.integration.ip.tcp.connection.TcpNetClientConnectionFactory;
import org.springframework.integration.ip.tcp.connection.TcpNetServerConnectionFactory;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@EnableIntegration
@IntegrationComponentScan
@Configuration
@ConfigurationProperties
public class VirtualizationTcpGatewayConfig {

    @Getter
    @Setter
    @Value("${mocker.tcp.server.port}")
    private int port;

    @Getter
    @Setter
    @Value("${mocker.target.service.tcp.location.hostname}")
    private String hostname;

    @Getter
    @Setter
    @Value("${mocker.target.service.tcp.location.tcpport}")
    private int tcpport;

    @Bean
    @ServiceActivator(inputChannel = "targetTcpServer")
    public MessageHandler tcpOutGate(AbstractClientConnectionFactory connectionFactory) {
        TcpOutboundGateway gate = new TcpOutboundGateway();
        gate.setConnectionFactory(connectionFactory);
        gate.setOutputChannelName("resultToString");
        return gate;
    }

    @Bean
    public TcpInboundGateway thisTcpServerInGateway(AbstractServerConnectionFactory connectionFactory) {
        TcpInboundGateway inGate = new TcpInboundGateway();
        inGate.setConnectionFactory(connectionFactory);
        inGate.setRequestChannel(thisTcpServer());
        return inGate;
    }

    @Bean
    public MessageChannel thisTcpServer() {
        return new DirectChannel();
    }

    @Bean
    public AbstractClientConnectionFactory targetTcpServerConnectionFactory() {
        return new TcpNetClientConnectionFactory(hostname, tcpport);
    }

    @Bean
    public AbstractServerConnectionFactory thisTcpSererConnectionFactory() {
        return new TcpNetServerConnectionFactory(this.port);
    }

    @Bean
    public ProxyVirtualizationServiceActivator proxyVirtualizationServiceActivator() {
        return new ProxyVirtualizationServiceActivator();
    }


}


