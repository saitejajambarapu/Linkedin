package com.api_gateway.api_gateway.filters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class LoggingGlobalFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("pre filter");
        return chain.filter(exchange).then(Mono.fromRunnable(()->{
            log.info("post filter");
        }));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}

//flow of the request

//Client Request
//   ↓
//           [GlobalFilter - order -1]
//        → log "pre filter"
//        ↓
//        [Other filters]
//        ↓
//        [Route handling / service call]
//        ↓
//        [Response received]
//        ↑
//        [GlobalFilter post logic]
//        → log "post filter"
//        ↑
//Response sent to client
