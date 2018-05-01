package com.gm.virtualization.cli;

import com.gm.virtualization.api.service.VirtualizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import static java.util.Arrays.stream;

public class VirtualizationServiceCli implements CommandLineRunner {
    public static final String MOCK = "mock";
    @Autowired
    private VirtualizationService virtualizationService;

    @Override
    public void run(String... args) throws Exception {


        final StringBuilder mockServiceUrl = new StringBuilder();
        stream(args).forEach(arg -> {
            if (arg.contains(MOCK) && arg.contains("=")) {
                mockServiceUrl.append(arg.split("=")[1]);
            }
        });

        if (mockServiceUrl.length() > 0) {
            virtualizationService.setLocation(mockServiceUrl.toString());
        }

    }

}
