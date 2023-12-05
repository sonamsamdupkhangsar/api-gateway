package me.sonam.apigateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtTokenFilter extends AbstractGatewayFilterFactory<JwtTokenFilter.Config> {
    private static final Logger LOG = LoggerFactory.getLogger(JwtTokenFilter.class);

    public JwtTokenFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {


            // Pre-processing
            if (config.isPreLogger()) {
                LOG.info("response headers {}",  exchange.getResponse().getHeaders());
                LOG.info("Pre GatewayFilter logging: {}", config.getBaseMessage());

            }
            return chain.filter(exchange)
                    .then(Mono.fromRunnable(() -> {
                        // Post-processing
                if (config.isPostLogger()) {
                    LOG.info("response headers {}",  exchange.getResponse().getHeaders());
                    LOG.info("Post GatewayFilter logging: {}", config.getBaseMessage());
                }
            }));
        });
    }

    public static class Config {
        private String baseMessage;
        private boolean preLogger;
        private boolean postLogger;
        public Config() {

        }

        public Config(String baseMessage, boolean preLogger, boolean postLogger) {
            this.baseMessage = baseMessage;
            this.preLogger = preLogger;
            this.postLogger = postLogger;
        }

        public String getBaseMessage() {
            return this.baseMessage;
        }

        public boolean isPreLogger() {
            return preLogger;
        }

        public boolean isPostLogger() {
            return postLogger;
        }
    }
}
