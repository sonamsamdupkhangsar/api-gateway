package me.sonam.apigateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {
    private static final Logger LOG = LoggerFactory.getLogger(CorsConfig.class);

    @Value("${allowedOrigins}")
    private String allowedOrigins; //csv allow origins

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfig = new CorsConfiguration();

        allowedOrigins = allowedOrigins.replace(" ", ""); //remove all whitespace
        List<String> list = Arrays.asList(allowedOrigins.split(","));
        LOG.info("adding allowedOrigins: {}", list);


        corsConfig.setAllowedOrigins(list);
        corsConfig.setMaxAge(8000L);
        corsConfig.addAllowedMethod("*");
        corsConfig.addAllowedHeader("*");

        corsConfig.addAllowedHeader("Content-Type");
        corsConfig.addAllowedHeader("api_key");
        corsConfig.addAllowedHeader("Authorization");
        corsConfig.addAllowedHeader("Origin");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        return source;
    }
}
