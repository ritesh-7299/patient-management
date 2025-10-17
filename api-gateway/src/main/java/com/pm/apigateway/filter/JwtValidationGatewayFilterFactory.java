package com.pm.apigateway.filter;

import com.pm.apigateway.service.SecurityService;
import com.pm.commonmodels.exceptions.UnauthorizedException;
import com.pm.commonmodels.security.AuthUser;
import com.pm.commonmodels.security.AuthUserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
public class JwtValidationGatewayFilterFactory extends AbstractGatewayFilterFactory {
    private static final Logger log = LoggerFactory.getLogger(JwtValidationGatewayFilterFactory.class);
    private final SecurityService securityService;

    public JwtValidationGatewayFilterFactory(
            SecurityService securityService) {
        this.securityService = securityService;
    }

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            String token = exchange
                    .getRequest()
                    .getHeaders()
                    .getFirst(HttpHeaders.AUTHORIZATION);

            if (token == null || !token.startsWith("Bearer ")) {
                throw new UnauthorizedException();
            }
            //here we need to verify the token
            AuthUser res = securityService.parseToken(token.substring(7));
            if (res == null) {
                throw new UnauthorizedException();
            }
            ServerHttpRequest sRequest = exchange.getRequest()
                    .mutate().header("user", AuthUserMapper.toString(res)).build();
            ServerWebExchange newExchange = exchange.mutate().request(sRequest).build();
            return chain.filter(newExchange);
        };
    }

    public Mono<Void> handleUnauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        String message = "Unauthorized access";
        DataBuffer buffer = exchange.getResponse()
                .bufferFactory()
                .wrap(message.getBytes(StandardCharsets.UTF_8));
        exchange.getResponse().writeWith(Mono.just(buffer));
        return exchange.getResponse().setComplete();
    }
}
