package com.study.employeeservice.service;

import com.study.employeeservice.config.FeignConfiguration;
import com.study.employeeservice.model.DepartmentDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import reactor.core.publisher.Mono;

@FeignClient(name = "DEPARTMENT-SERVICE", configuration = FeignConfiguration.class)
public interface DepartmentServiceFeignClient {
    @GetMapping("/api/departments/v1/code/{departmentCode}")
    Mono<DepartmentDto> getDepartmentByCode(@PathVariable String departmentCode);
}
