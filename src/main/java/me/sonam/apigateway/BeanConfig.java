package me.sonam.apigateway;

import io.kubernetes.client.openapi.auth.HttpBearerAuth;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Optional;

@Configuration
public class BeanConfig {
    private static final Logger LOG = LoggerFactory.getLogger(BeanConfig.class);

    /**
     * For public endpoints set this publicEndpointKeyResolver configuration
     * get the ip address and rate limit
     * @return
     */
    //@Bean
    KeyResolver publicEndpointKeyResolver() {
        LOG.info("create public key resolver by ipaddress");
        return exchange ->
                Optional.ofNullable(exchange.getRequest().getRemoteAddress())
                                .map(InetSocketAddress::getAddress)
                                        .map(InetAddress::getHostAddress)
                                                .map(Mono::just)
                                                        .orElse(Mono.empty());
                /*Mono.just(exchange.getRequest().getRemoteAddress().getAddress().getHostAddress())
                        .switchIfEmpty(Mono.empty());*/
    }

    //@Bean
    KeyResolver privateEndpointKeyResolver() {
        LOG.info("create private key resolver using token");
        return exchange ->
                Optional.ofNullable(exchange.getRequest().getHeaders())
                        .map(httpHeaders -> httpHeaders.getFirst(HttpHeaders.AUTHORIZATION))
                        .map(Mono::just)
                        .orElse(Mono.empty());
    }
}
