package com.study.employeeservice.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@LoadBalancerClient(name = "DEPARTMENT-SERVICE", configuration = DepartmentServiceConfiguration.class)
public class WebClientConfig {
    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
    }

  @LoadBalanced
  @Bean
  @Qualifier("loadBalancedWebClientBuilder")
  public WebClient.Builder webClientBuilder() {
    return WebClient.builder();
  }
}
