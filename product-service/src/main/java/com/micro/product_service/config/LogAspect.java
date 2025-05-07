package com.micro.product_service.config;

import com.micro.product_service.dto.LogEvent;
import com.micro.product_service.service.LogProducerService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Aspect
@Component
@RequiredArgsConstructor
public class LogAspect {

    private final LogProducerService logProducerService;

    @Around("@within(org.springframework.stereotype.Service) && !@annotation(com.micro.user_service.annotation.LogIgnore)")
    public Object logServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        LocalDateTime logDate = LocalDateTime.now();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        String serviceName = "user-service";
        String serviceURL = request.getRequestURI();
        String method = request.getMethod();

        String message = "Request to " + serviceURL + " with method " + method;
        Object result;
        HttpStatus status;

        try {
            result = joinPoint.proceed();

            status = HttpStatus.OK;
            logProducerService.sendLog(LogEvent.builder()
                    .message(message)
                    .serviceName(serviceName)
                    .serviceURL(serviceURL)
                    .statusCode(status.value() + " " + status.getReasonPhrase())
                    .logDate(logDate)
                    .build());

            return result;
        } catch (Throwable ex) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;

            logProducerService.sendLog(LogEvent.builder()
                    .message(message + " failed with exception: " + ex.getMessage())
                    .serviceName(serviceName)
                    .serviceURL(serviceURL)
                    .statusCode(status.value() + " " + status.getReasonPhrase())
                    .logDate(logDate)
                    .build());

            throw ex;
        }
    }

}
