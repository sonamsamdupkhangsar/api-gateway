package me.sonam.apigateway;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
		properties = {"spring.cloud.discovery.enabled = false"})
public class ApiGatewayApplicationTests {

	@Autowired
	WebTestClient webTestClient;

	@MockBean
	ReactiveJwtDecoder jwtDecoder;


	@BeforeAll
	static void loadProperty() {
		System.setProperty("OKTA_CLIENT_SECRET", "test");
		System.setProperty("EUREKA_USER", "test");
		System.setProperty("EUREKA_PASSWORD", "test");
	}

	@Test
	public void testCorsConfiguration() {
		Jwt jwt = jwt();
		when(this.jwtDecoder.decode(anyString())).thenReturn(Mono.just(jwt));
		WebTestClient.ResponseSpec response = webTestClient.put().uri("/")
				.headers(addJwt(jwt))
				.header("Origin", "http://example.com")
				.exchange();

		response.expectHeader().valueEquals("Access-Control-Allow-Origin", "*");
	}

	private Jwt jwt() {
		return new Jwt("token", null, null,
				Map.of("alg", "none"), Map.of("sub", "betsy"));
	}

	private Consumer<HttpHeaders> addJwt(Jwt jwt) {
		return headers -> headers.setBearerAuth(jwt.getTokenValue());
	}
}