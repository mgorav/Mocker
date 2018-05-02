package com.gm.virtualization.application.filter;

import com.gm.virtualization.api.VirtualizationServiceApi;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

import static com.gm.virtualization.application.filter.SlaUtils.slaLogging;
import static java.lang.System.currentTimeMillis;
import static java.util.Objects.nonNull;

/**
 * SLA time logging from the time request reached the {@link VirtualizationServiceApi}
 * till the request has been processed.
 */
public class VirtualizationFilter extends OncePerRequestFilter {
    private static final Log log = LogFactory.getLog(VirtualizationFilter.class);

    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {


        String name = "";


        long start = currentTimeMillis();

        try {

            Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();
            HandlerExecutionChain handler = requestMappingHandlerMapping.getHandler((HttpServletRequest) request);

            if (nonNull(handler)) {
                HandlerMethod handlerMethod = (HandlerMethod) handler.getHandler();
                name = handlerMethod.getMethod().getName();
            }

            response.reset();
            chain.doFilter(request, response);
        } catch (Exception e) {
            log.error(e);
        } finally {
            long end = currentTimeMillis();
            long time = end - start;

            slaLogging(log, time, name);


        }
    }

    /**
     * Called by the web container to indicate to a filter that it is being
     * taken out of service. This method is only called once all threads within
     * the filter's doFilter method have exited or after a timeout period has
     * passed. After the web container calls this method, it will not call the
     * doFilter method again on this instance of the filter. <br>
     * <br>
     * <p>
     * This method gives the filter an opportunity to clean up any resources
     * that are being held (for example, memory, file handles, threads) and make
     * sure that any persistent state is synchronized with the filter's current
     * state in memory.
     */
    @Override
    public void destroy() {

    }

}
