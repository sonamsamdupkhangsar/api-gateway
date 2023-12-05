package me.sonam.apigateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class OriginMdcGlobalFilter implements GlobalFilter {
    private static final Logger LOG = LoggerFactory.getLogger(OriginMdcGlobalFilter.class);
    private static final String REQUEST_ID = "requestId";


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        LOG.info("in global filter");
        final String value = "http://api-gateway:8080";

        if (exchange.getRequest().getHeaders().containsKey(REQUEST_ID)) {
            LOG.info("contains requestId already: {}", exchange.getRequest().getHeaders().getFirst(REQUEST_ID));
            LOG.info("just add the requestId to MDC: {}", exchange.getRequest().getHeaders().getFirst(REQUEST_ID));
            MDC.put(REQUEST_ID, exchange.getRequest().getHeaders().getFirst(REQUEST_ID));

            return chain.filter(exchange.mutate().build()).then(Mono.fromRunnable(() -> {
                LOG.info("remove MDC requestid from log");
                MDC.remove("requestId");
            }));
        }
        else {
            UUID uuid = UUID.randomUUID();
            LOG.info("create a request UUID and add to header: {}", uuid);

            MDC.put(REQUEST_ID, uuid.toString());
          //  LOG.info("add http://api-gateway:9001 to Origin header");
            ServerHttpRequest request = exchange.getRequest()
                    .mutate()
                    .header("requestId", uuid.toString())
                    //.header("Origin", "http://api-gateway:9001")
                    .build();

            ServerWebExchange exchange1 = exchange.mutate().request(request).build();
            return chain.filter(exchange1).then(Mono.fromRunnable(() -> {
                        LOG.info("after adding MDC remove MDC requestId from log");
                        MDC.remove("requestId");
                    }));
        }
    }
}
