package com.study.employeeservice.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.lang.NonNull;

@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
public class ExceptionMessage {
    private String timestamp;

    @NonNull
    private Integer status;

    private String error;

    @NonNull
    private String message;

    private String path;
}