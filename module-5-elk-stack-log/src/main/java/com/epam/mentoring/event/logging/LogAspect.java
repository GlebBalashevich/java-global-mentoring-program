package com.epam.mentoring.event.logging;

import java.net.InetAddress;
import java.net.UnknownHostException;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.ThreadContext;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class LogAspect {

    @Value("${spring.application.name}")
    private String appName;

    @Value("${spring.application.version}")
    private String appVersion;

    @Pointcut("@annotation(org.springframework.context.event.EventListener)")
    public void appReadyEvent() {
    }

    @Before("appReadyEvent()")
    public void updateEventContext() throws UnknownHostException {
        ThreadContext.push("app_name=" + appName);
        ThreadContext.push("app_version=" + appVersion);
        ThreadContext.push("host_name=" + InetAddress.getLocalHost().getHostName());
    }

}
