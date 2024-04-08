package com.study.employeeservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

@Slf4j
@RefreshScope
@Service
public class MessageService {
    @Value("${message}")
    private String message;

    public String getMessage() {
        log.info("Returning message from MessageService. message={}", message);
        return message;
    }
}
