package com.gm.virtualization.application.configurer;

import com.gm.virtualization.application.filter.VirtualizationFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.lang.Integer.MAX_VALUE;
import static java.util.Arrays.asList;

/**
 * Configures {@link VirtualizationFilter}
 */
@Configuration
public class VirtualizationFilterConfigurer {

    @Bean
    public FilterRegistrationBean iinServiceFilterBean() {
        FilterRegistrationBean frb = new FilterRegistrationBean();
        frb.setFilter(mockerFilter());
        frb.setOrder(MAX_VALUE);
        frb.setUrlPatterns(asList("/*"));
        return frb;
    }

    @Bean
    public VirtualizationFilter mockerFilter() {

        return new VirtualizationFilter();
    }


}
