package com.study.employeeservice.controller;

import com.study.employeeservice.model.APIResponseDto;
import com.study.employeeservice.model.EmployeeDto;
import com.study.employeeservice.service.EmployeeService;
import io.micrometer.observation.annotation.Observed;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Observed
@RestController
@RequestMapping("/api/employees/v1")
public class EmployeeController {
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }
    @GetMapping
    public ResponseEntity<Flux<EmployeeDto>> getAllEmployees(){
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Mono<APIResponseDto>> getEmployeeById(@PathVariable Long id){
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    @PostMapping
    public ResponseEntity<Mono<EmployeeDto>> create(@RequestBody EmployeeDto request){
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.create(request));
    }
    @PutMapping("/{id}")
    public ResponseEntity<Mono<EmployeeDto>> updateEmployee(@PathVariable Long id, @RequestBody EmployeeDto request){
        return ResponseEntity.ok(employeeService.updateEmployee(id, request));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Mono<Void>> deleteEmployee(@PathVariable Long id){
        return ResponseEntity.ok(employeeService.deleteEmployee(id));
    }
}
