package com.study.employeeservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.InputStream;

public class RetreiveMessageErrorDecoder implements ErrorDecoder {
    private ErrorDecoder errorDecoder = new Default();
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Exception decode(String methodKey, Response response) {
        FeignException exception = feign.FeignException.errorStatus(methodKey, response);
        HttpStatus httpStatus = HttpStatus.valueOf(response.status());
        return getError(exception, response, httpStatus, methodKey);
    }

    private Exception getError(FeignException exception,
                               Response response,
                               HttpStatus httpStatus,
                               String methodKey) {
        if (httpStatus.is5xxServerError()) {

            return new RetryableException(
                    response.status(),
                    exception.getMessage(),
                    response.request().httpMethod(),
                    (Throwable) exception,
                    (Long) null,
                    response.request()
            );
        } else if (httpStatus.is4xxClientError()) {
            try {
                InputStream inputStream = response.body().asInputStream();
                ExceptionMessage exceptionMessage = objectMapper.readValue(inputStream, ExceptionMessage.class);
                return new FeignException.FeignClientException(
                        exceptionMessage.getStatus(),
                        exceptionMessage.getMessage(),
                        response.request(),
                        response.body().asInputStream().readAllBytes(),
                        response.request().headers()
                );
            } catch (IOException e) {
                return new Exception(exception.getMessage());
            }
        }
        return errorDecoder.decode(methodKey, response);
    }
}