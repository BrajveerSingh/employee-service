package com.study.employeeservice.controller;

import com.study.employeeservice.service.MessageService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/emp/message")
    public String getMessage() {
        return messageService.getMessage();
    }
}
