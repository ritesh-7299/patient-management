package com.pm.apigateway.exception;

import com.pm.commonmodels.exceptions.UnauthorizedException;
import com.pm.commonmodels.response.ErrorResponse;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@RestControllerAdvice
public class JwtValidationException {

    @ExceptionHandler(WebClientResponseException.Unauthorized.class)
    public Mono<Void> handleUnauthorizedException(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        String message = "Unauthorized access";
        System.out.println(message);
        DataBuffer buffer = exchange.getResponse()
                .bufferFactory()
                .wrap(message.getBytes(StandardCharsets.UTF_8));
        exchange.getResponse().writeWith(Mono.just(buffer));
        return exchange.getResponse().setComplete();
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse<?>> handleUnauthorizedException() {
        return new ResponseEntity<ErrorResponse<?>>(new ErrorResponse<String>("Unauthorized"), HttpStatus.UNAUTHORIZED);
    }

}
