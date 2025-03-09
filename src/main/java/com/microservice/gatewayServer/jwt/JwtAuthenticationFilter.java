package com.microservice.gatewayServer.jwt;

import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Date;

@Component
public class JwtAuthenticationFilter implements WebFilter {
    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    private Mono<Void> getVoidMono(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json");

        String jsonResponse = String.format(
                "{\"timestamp\": %d, \"statusCode\": %d, \"status\": \"Unauthorized\", \"message\": \"Authentication token is missing or invalid\"}",
                new Date().getTime(),
                HttpStatus.UNAUTHORIZED.value()
        );
        return response.writeWith(Mono.just(response.bufferFactory().wrap(jsonResponse.getBytes())));
    }
    @Override
    @NonNull
    public Mono<Void> filter(@NonNull ServerWebExchange exchange,@NonNull WebFilterChain chain) {
        String url = exchange.getRequest().getURI().getPath();
        System.out.println("url: " + url);
        if(url.contains("/auth/")) {
            return chain.filter(exchange).then(Mono.fromRunnable(() -> System.out.println("Url with Auth: " + url)));
        }
        String authorizationHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        String jwt;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                if(jwtUtil.validateToken(jwt))
                {
                    return chain.filter(exchange).then(Mono.fromRunnable(() -> System.out.println("Url without Auth: " + url)));
                }
            } catch (Exception e) {
                return getVoidMono(exchange);
            }
        }
        return getVoidMono(exchange);
    }
}
