package com.example.is_web_binder_used_to_bind_jackson_dto_request_body;

import java.net.URI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import static com.example.is_web_binder_used_to_bind_jackson_dto_request_body.IsWebBinderUsedToBindJacksonDtoRequestBodyApplication.PASSWORD;
import static com.example.is_web_binder_used_to_bind_jackson_dto_request_body.IsWebBinderUsedToBindJacksonDtoRequestBodyApplication.USER_1_USER_NAME;
import static com.example.is_web_binder_used_to_bind_jackson_dto_request_body.IsWebBinderUsedToBindJacksonDtoRequestBodyApplication.USER_2_USER_NAME;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IsWebBinderUsedToBindJacksonDtoRequestBodyApplicationTests {

	private static final String SCHEME = "http";
	private static final String HOST = "localhost";

	@LocalServerPort
	protected int port;

	private URI searchMethodURI;

	@BeforeEach
	void setUp() {
		searchMethodURI = UriComponentsBuilder.newInstance()
				.scheme(SCHEME)
				.host(HOST)
				.port(port)
				.path(SomeController.CONTROLLER_PATH + SomeController.SEARCH_METHOD).build().toUri();
	}

	@Test
	void testInvalidAuthorized() {
		callTest(false, true, HttpStatus.BAD_REQUEST);
	}

	@Test
	void testInvalidUnauthorized() {
		callTest(false, false, HttpStatus.FORBIDDEN);
	}

	@Test
	void testValidAuthorized() {
		callTest(true, true, HttpStatus.OK);
	}

	@Test
	void testValidUnauthorized() {
		callTest(true, false, HttpStatus.FORBIDDEN);
	}

	private void callTest(boolean validBody, boolean authorized, HttpStatus expectedStatus) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		String body = createBody(validBody);
		ResponseEntity<String> exchange = createTestRestTemplateFor(authorized ? USER_2_USER_NAME : USER_1_USER_NAME)
				.exchange(searchMethodURI, HttpMethod.POST, new HttpEntity<>(body, headers), String.class);

		assertThat(exchange.getStatusCode().value(), is(expectedStatus.value()));
	}


	private TestRestTemplate createTestRestTemplateFor(String userName) {
		return new TestRestTemplate(userName, PASSWORD);
	}

	private String createBody(boolean valid) {
		return """
		{"userId":%d}
		""".formatted(valid ? Integer.MAX_VALUE : Integer.MIN_VALUE);
	}
}