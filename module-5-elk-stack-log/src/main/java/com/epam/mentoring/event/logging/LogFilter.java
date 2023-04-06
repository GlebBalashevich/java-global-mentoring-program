package com.epam.mentoring.event.logging;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.catalina.connector.RequestFacade;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(value = Ordered.HIGHEST_PRECEDENCE)
public class LogFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        ThreadContext.push(UUID.randomUUID().toString());
        ThreadContext.push("path=" + ((RequestFacade) request).getServletPath());
        ThreadContext.push("method=" + ((RequestFacade) request).getMethod());
        chain.doFilter(request, response);
    }

}
