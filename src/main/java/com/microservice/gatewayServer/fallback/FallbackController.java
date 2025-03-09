package com.microservice.gatewayServer.fallback;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/fallback")
public class FallbackController {
    @GetMapping("/user")
    public Mono<String> userFallback() {
        return Mono.just("User Service is currently unavailable. Please try again later.");
    }

    @GetMapping("/task")
    public Mono<String> taskFallback() {
        return Mono.just("Task Service is currently unavailable. Please try again later.");
    }
}
