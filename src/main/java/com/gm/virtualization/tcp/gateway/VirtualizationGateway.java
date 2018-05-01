package com.gm.virtualization.tcp.gateway;

import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway(defaultRequestChannel = "targetTcpServer")
public interface VirtualizationGateway {
    String viaTcp(String in);
}
