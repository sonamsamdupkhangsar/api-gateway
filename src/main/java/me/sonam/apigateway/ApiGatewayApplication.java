package me.sonam.apigateway;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
	@Value("${eureka.client.serviceUrl.defaultZone}")
	private String hostUrl;

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

	@PostConstruct
	public void logHostUrl() {
		LOG.info("hostUrl: {}", hostUrl);
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

/*
@Data
class Car {
	private String name;
	private LocalDate releaseDate;

}
*/

//@RestController
//class FaveCarsController {

	//private final WebClient.Builder carClient;

	/*public FaveCarsController(WebClient.Builder carClient) {
		this.carClient = carClient;
	}*/

	/*@GetMapping("/old-fave-cars")
	public Flux<Car> faveCars() {
		return carClient.build().get().uri("lb://car-service/cars")
				.retrieve().bodyToFlux(Car.class)
				.filter(this::isFavorite);
	}*/

	/*@GetMapping("/fave-cars")
	public Flux<Car> faveCars(@RegisteredOAuth2AuthorizedClient("okta") OAuth2AuthorizedClient authorizedClient) {
		return carClient.build().get().uri("lb://car-service/cars")
				.header("Authorization", "Bearer " + authorizedClient.getAccessToken().getTokenValue())
				.retrieve().bodyToFlux(Car.class)
				.filter(this::isFavorite);
	}*/

	/*private boolean isFavorite(Car car) {
		return car.getName().equals("ID. BUZZ");
	}*/
//}

/*
@RestController
class CarsFallback {
	private static final Logger LOG = LoggerFactory.getLogger(CarsFallback.class);

	@GetMapping("/cars-fallback")
	public Flux<Car> noCars() {
		LOG.info("fallback no cars");
		return Flux.empty();
	}
}*/
