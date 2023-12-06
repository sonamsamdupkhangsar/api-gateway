package me.sonam.apigateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.UUID;

import static org.springframework.http.HttpHeaders.ORIGIN;

@Service
public class OriginMdcGlobalFilter implements GlobalFilter {
    private static final Logger LOG = LoggerFactory.getLogger(OriginMdcGlobalFilter.class);
    private static final String REQUEST_ID = "X-Request-ID";

    @Value("${originValue}")
    private String originValue;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        LOG.info("in global filter");

        String xRequestId;

        if (exchange.getRequest().getHeaders().containsKey(REQUEST_ID)) {
            xRequestId = exchange.getRequest().getHeaders().getFirst(REQUEST_ID);

            LOG.info("just add the requestId to MDC: {}", xRequestId);
        }
        else {
            LOG.info("create a UUID for xRequestid when no header of {}", REQUEST_ID);
            xRequestId = UUID.randomUUID().toString();
        }

        MDC.put(REQUEST_ID, xRequestId);
        ServerHttpRequest request = exchange.getRequest()
                .mutate()
                .header("requestId", xRequestId)
                .build();

        if (exchange.getRequest().getMethod().equals(HttpMethod.POST)) {
            if (request.getHeaders().getFirst(ORIGIN) == null || request.getHeaders().getFirst(ORIGIN).equals("null")) {
                LOG.info("add origin header when it's null: {}", originValue);
                request = request.mutate().header("Origin", originValue).build();
            }
            else {
                LOG.info("Origin is already set to: {}", request.getHeaders().getFirst(ORIGIN));
            }
        }

        ServerWebExchange exchange1 = exchange.mutate().request(request).build();
        return chain.filter(exchange1).transform((call) ->call.doFinally(signalType -> {
            LOG.info("response headers: {}", exchange1.getResponse().getHeaders());
            LOG.info("statusCode: {}", exchange1.getResponse().getStatusCode());
            if (exchange1.getResponse().getHeaders().getLocation() != null) {
                final String path = exchange1.getResponse().getHeaders().getLocation().getPath();
                final String rawPath = exchange1.getResponse().getHeaders().getLocation().getRawPath();
                String query = exchange1.getResponse().getHeaders().getLocation().getQuery();
                if (query == null) {
                    query = "";
                }
                LOG.info("redirectin path: {}, rawPath: {}, query: {}", path, rawPath, query);
                exchange1.getResponse().getHeaders().setLocation(URI.create("http://api-gateway:8080" + path+"?"+query));
            }
        })).then(Mono.fromRunnable(() -> {
            LOG.info("after adding MDC remove MDC requestId from log");
            MDC.remove("requestId");
        }));
    }
}
