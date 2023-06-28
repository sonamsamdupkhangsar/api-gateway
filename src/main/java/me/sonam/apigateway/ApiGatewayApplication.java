package me.sonam.apigateway;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;


@SpringBootApplication(scanBasePackages = {"me.sonam.security", "me.sonam.apigateway"})
public class ApiGatewayApplication {
	private static final Logger LOG = LoggerFactory.getLogger(ApiGatewayApplication.class);

	@Value("${eureka.client.serviceUrl.defaultZone}")
	private String hostUrl;

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}
	/*@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder, JwtTokenFilter filterFactory) {//TokenRelayGatewayFilterFactory filterFactory) {
		return builder.routes()
				.route("car-service", r -> r.path("/cars")
						//.filters(f -> f.filter(filterFactory.apply()))
						.uri("lb://car-service"))
				//.build()
				.route("user-service", r-> r.path("/users")
						.filters(f -> f.filter(filterFactory.apply(new JwtTokenFilter.Config("My Cusom Message", true, true))))
						.uri("lb://user-rest-service"))
				.route("user-service", r-> r.path("/users/api/health/*")
						.filters(f -> f.filter(filterFactory.apply(new JwtTokenFilter.Config("My Cusom Message", true, true))))
						.uri("lb://user-rest-service"))
				.route("account-rest-service",
						r-> r.path(
 								"/accounts/validate/secret/{authenticationId}/{secret}",
								"/accounts/email/authenticationId/{email}",
								"/accounts/emailmysecret/{authenticationId}",
								"/accounts/emailactivationlink/{authenticationId}"
								)
						.uri("lb://account-rest-service"))
				.route("account-rest-service",
						r-> r.path(
								"/authentications/password"  //password reset
								)
								.uri("lb://authentication-rest-service"))


				.route("account-rest-service", r-> r.path("/accounts/**")
						.uri("lb://account-rest-service"))
				.build();
				*//*.route("car-service", r -> r.path("/cars")
						.filters(f -> f.hystrix(c -> c.setName("carsFallback")
								.setFallbackUri("forward:/cars-fallback")))
						.uri("lb://car-service/cars"))
						// need to define a hystrix bean which is not in GA release from Spring
				.build();*//*
	}*/

	@PostConstruct
	public void logHostUrl() {
		LOG.info("hostUrl: {}", hostUrl);
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
