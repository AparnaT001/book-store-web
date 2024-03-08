package com.demo.bookstore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

	public static final String USER_TAG = "Account Registration";

	public static final String BOOK_TAG = "Book Store";

	public static final String CART_TAG = "Shopping cart";

	@Bean
	public OpenAPI swaggerOpenAPI() {
		Info info = new Info();
		info.setTitle("Book Store");
		info.setVersion("1.0");
		return new OpenAPI()
				.components(new Components())
				.info(info);
	}
}
