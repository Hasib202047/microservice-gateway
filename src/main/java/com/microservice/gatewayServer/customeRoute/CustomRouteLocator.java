package com.microservice.gatewayServer.customeRoute;

import com.microservice.gatewayServer.fallback.FallbackController;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomRouteLocator {

    @Bean
    public RouteLocator customGatewayRoutes(RouteLocatorBuilder routeLocatorBuilder) {
        return routeLocatorBuilder.routes()
                // Route for "user" service
                .route("user-service", r -> r.path("/user/**") // Match paths starting with /user
                        .filters(f -> f
                                .stripPrefix(1) // Remove the first part of the path (e.g., /user)
                                .circuitBreaker(c -> c
                                        .setName("userCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/user")) // Fallback URI
                        )
                        .uri("lb://USER-SERVICE") // "lb://" tells Gateway to use Eureka for load balancing
                )
                // Route for "task" service
                .route("task-service", r -> r.path("/task/**") // Match paths starting with /task
                        .filters(f -> f
                                .stripPrefix(1) // Remove the first part of the path (e.g., /task)
//                                .circuitBreaker(c -> c
//                                        .setName("taskCircuitBreaker")
//                                        .setFallbackUri("forward:/fallback/task"))
                        )
                        .uri("lb://TASK-SERVICE") // "lb://" tells Gateway to use Eureka for load balancing
                )
                .build();
    }

    @Bean
    public FallbackController fallbackController() {
        return new FallbackController();
    }

}
