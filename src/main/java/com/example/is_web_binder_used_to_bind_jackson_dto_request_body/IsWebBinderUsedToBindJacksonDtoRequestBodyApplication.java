package com.example.is_web_binder_used_to_bind_jackson_dto_request_body;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@SuppressWarnings("deprecation")
@SpringBootApplication
@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true, offset = Integer.MAX_VALUE)
public class IsWebBinderUsedToBindJacksonDtoRequestBodyApplication {

	public static final String ROLE1 = "ROLE1";
	public static final String ROLE2 = "ROLE2";

	//yes, not cool. It's mwe, I don't want to spend extra time one this.
	static final String PASSWORD = "password";
	static final String USER_1_USER_NAME = "user1";
	static final String USER_2_USER_NAME = "user2";

	public static void main(String[] args) {
		SpringApplication.run(IsWebBinderUsedToBindJacksonDtoRequestBodyApplication.class, args);
	}

	@Bean
	public UserDetailsService userDetailsService() {
		// Define an in-memory user with roles
		UserDetails user1 = User.withDefaultPasswordEncoder()
				.username(USER_1_USER_NAME)
				.password("password")
				.roles(ROLE1)
				.build();

		UserDetails user2 = User.withDefaultPasswordEncoder()
				.username(USER_2_USER_NAME)
				.password("password")
				.roles(ROLE2)
				.build();

		return new InMemoryUserDetailsManager(user1, user2);
	}

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http
				.csrf(AbstractHttpConfigurer::disable)
				.authorizeHttpRequests(a -> a.anyRequest().permitAll())
				.httpBasic(conf -> conf.realmName("aaa"))
				.build();
	}
}
