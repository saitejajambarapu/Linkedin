package com.api_gateway.api_gateway.filters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

//paste - name: LoggingInventoryfilter in the required route to apply this logging

@Component
@Slf4j
public class LoggingInventoryfilter extends AbstractGatewayFilterFactory<LoggingInventoryfilter.Config> {

    public LoggingInventoryfilter(){
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            log.info("inventory filter pre: {}", exchange.getRequest().getURI());
            return (chain.filter(exchange));
        });
    }

    public static class  Config{
    }
}
