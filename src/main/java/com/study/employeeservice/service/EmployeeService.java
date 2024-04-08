package com.study.employeeservice.service;

import com.study.employeeservice.enitity.Employee;
import com.study.employeeservice.model.APIResponseDto;
import com.study.employeeservice.model.DepartmentDto;
import com.study.employeeservice.model.EmployeeDto;
import com.study.employeeservice.repository.EmployeeRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final RestTemplate restTemplate;
    private final WebClient webClient;
    private final DepartmentServiceFeignClient departmentServiceFeignClient;


    private final WebClient.Builder loadBalancedWebClientBuilder;
    private final ReactorLoadBalancerExchangeFilterFunction lbFunction;

    public EmployeeService(EmployeeRepository employeeRepository,
                           RestTemplate restTemplate,
                           WebClient webClient,
                           DepartmentServiceFeignClient departmentServiceFeignClient,
                           @Qualifier("loadBalancedWebClientBuilder") WebClient.Builder loadBalancedWebClientBuilder,
                           ReactorLoadBalancerExchangeFilterFunction lbFunction) {
        this.employeeRepository = employeeRepository;
        this.restTemplate = restTemplate;
        this.webClient = webClient;
        this.departmentServiceFeignClient = departmentServiceFeignClient;
        this.loadBalancedWebClientBuilder = loadBalancedWebClientBuilder;
        this.lbFunction = lbFunction;
    }

    public Flux<EmployeeDto> getAllEmployees() {
        return employeeRepository.findAll().map(
                employee -> new EmployeeDto(
                        employee.getId(),
                        employee.getFirstName(),
                        employee.getLastName(),
                        employee.getEmail(),
                        employee.getDepartmentCode()
                )
        );
    }

    @CircuitBreaker(name = "${spring.application.name}", fallbackMethod = "getEmployeeByIdFallback")
    public Mono<APIResponseDto> getEmployeeById(Long id) {
//        return employeeRepository.findById(id)
//                .map(
//                        this:: findUsingRestTemplate
//                );

        Mono<Mono<APIResponseDto>> monoMonoApiResponseDto =
                employeeRepository.findById(id)
                        .map(
                                this::findUsingWebClient
                        );
        return monoMonoApiResponseDto.flatMap(Function.identity());
//        return employeeRepository.findById(id)
//                .map(
//                        this::findUsingFeignClient
//                ).flatMap(Function.identity());

//        return employeeRepository.findById(id)
//                .flatMap(
//                        employee -> findUsingWebClientWithLoadBalacer(employee)
//                );
    }

    private Mono<? extends APIResponseDto> findUsingWebClientWithLoadBalacer(Employee employee) {
        return loadBalancedWebClientBuilder.build().get().uri("http://DEPARTMENT-SERVICE/api/departments/v1/code/" + employee.getDepartmentCode())
                .retrieve().bodyToMono(DepartmentDto.class)
                .map(
                        departmentDto -> new APIResponseDto(
                                new EmployeeDto(
                                        employee.getId(),
                                        employee.getFirstName(),
                                        employee.getLastName(),
                                        employee.getEmail(),
                                        employee.getDepartmentCode()
                                ),
                                departmentDto
                        )
                );

    }

    private Mono<APIResponseDto> findUsingFeignClient(Employee employee) {
//        DepartmentDto departmentDto = departmentServiceFeignClient.getDepartmentByCode(employee.getDepartmentCode());
        return departmentServiceFeignClient.getDepartmentByCode(employee.getDepartmentCode())
                .map(
                        departmentDto ->
                                new APIResponseDto(
                                        new EmployeeDto(
                                                employee.getId(),
                                                employee.getFirstName(),
                                                employee.getLastName(),
                                                employee.getEmail(),
                                                employee.getDepartmentCode()
                                        ),
                                        departmentDto
                                )
                );

    }

    private APIResponseDto findUsingRestTemplate(Employee employee) {
        ResponseEntity<DepartmentDto> departmentDtoResponseEntity =
                restTemplate.getForEntity(
                        "http://localhost:8080/api/departments/v1/code/" + employee.getDepartmentCode(),
                        DepartmentDto.class);

        DepartmentDto departmentDto = departmentDtoResponseEntity.getBody();
        return new APIResponseDto(
                new EmployeeDto(
                        employee.getId(),
                        employee.getFirstName(),
                        employee.getLastName(),
                        employee.getEmail(),
                        employee.getDepartmentCode()
                ),
                departmentDto
        );
    }

    private Mono<APIResponseDto> findUsingWebClient(Employee employee) {
        return webClient.get()
                .uri("http://localhost:8080/api/departments/v1/code/" + employee.getDepartmentCode())
                .retrieve()
                .bodyToMono(DepartmentDto.class)
                .map(
                        departmentDto -> new APIResponseDto(
                                new EmployeeDto(
                                        employee.getId(),
                                        employee.getFirstName(),
                                        employee.getLastName(),
                                        employee.getEmail(),
                                        employee.getDepartmentCode()
                                ),
                                departmentDto
                        )
                );


    }

    public Mono<EmployeeDto> create(EmployeeDto request) {
        return employeeRepository.save(
                new Employee(
                        null,
                        request.firstName(),
                        request.lastName(),
                        request.email(),
                        request.departmentCode()
                )
        ).map(
                employee -> new EmployeeDto(
                        employee.getId(),
                        employee.getFirstName(),
                        employee.getLastName(),
                        employee.getEmail(),
                        employee.getDepartmentCode()
                )
        );
    }

    public Mono<EmployeeDto> updateEmployee(Long id, EmployeeDto request) {
        return employeeRepository.findById(id).flatMap(
                employee -> {
                    employee.setFirstName(request.firstName());
                    employee.setLastName(request.lastName());
                    employee.setEmail(request.email());
                    return employeeRepository.save(employee);
                }
        ).map(
                employee -> new EmployeeDto(
                        employee.getId(),
                        employee.getFirstName(),
                        employee.getLastName(),
                        employee.getEmail(),
                        employee.getDepartmentCode()
                )
        );
    }

    public Mono<Void> deleteEmployee(Long id) {
        return employeeRepository.deleteById(id);
    }

    public Mono<APIResponseDto> getEmployeeByIdFallback(Long id, Exception exception) {
        return employeeRepository.findById(id)
                .map(
                        employee -> new APIResponseDto(
                                new EmployeeDto(
                                        employee.getId(),
                                        employee.getFirstName(),
                                        employee.getLastName(),
                                        employee.getEmail(),
                                        employee.getDepartmentCode()
                                ),
                                new DepartmentDto(
                                        null,
                                        "R&D",
                                        "Research and Development",
                                        "RD001"
                                )
                        )
                );
    }

}
