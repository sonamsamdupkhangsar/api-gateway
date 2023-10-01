package me.sonam.apigateway;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;


@SpringBootApplication(scanBasePackages = {"me.sonam.security", "me.sonam.apigateway"})
public class ApiGatewayApplication {
	private static final Logger LOG = LoggerFactory.getLogger(ApiGatewayApplication.class);

	@Autowired
	private DiscoveryClient discoveryClient;

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

	@PostConstruct
	public void logHostUrl() {
		List<String> serviceList = discoveryClient.getServices();
		LOG.info("printing services of size: {}", serviceList.size());

		serviceList.forEach(s -> LOG.info("Found service: {}", s));

	}

	@Bean
	@LoadBalanced
	public WebClient.Builder loadBalancedWebClientBuilder() {
		return WebClient.builder();
	}

	@LoadBalanced
	@Bean("noFilter")
	public WebClient.Builder webClientBuilderNoFilter() {
		LOG.info("returning for noFilter load balanced webclient part");
		return WebClient.builder();
	}
}
