package com.demo.bookstore.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.demo.bookstore.config.SwaggerConfig;
import com.demo.bookstore.exception.BookstoreException;
import com.demo.bookstore.models.request.ShoppingCartRequest;
import com.demo.bookstore.models.response.AppResponse;
import com.demo.bookstore.models.response.OrderResponse;
import com.demo.bookstore.models.response.Response;
import com.demo.bookstore.models.response.ShoppingCartResponse;
import com.demo.bookstore.service.ShoppingCartService;
import com.demo.bookstore.utility.AppUtility;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Aparna Thirumoorthy
 *
 */

@RestController("shopping/")
@Tag(name = SwaggerConfig.CART_TAG)
@Slf4j
public class ShoppingCartController {

	@Autowired
	private ShoppingCartService shoppingCartService;

	@Operation(description = "Checkout books")
	@PostMapping(value = "checkout")
	public ResponseEntity<Response> checkoutBooks(@Valid @RequestParam Long userId) {
		try {
			AppResponse<OrderResponse> response = new AppResponse<>();
			OrderResponse order = shoppingCartService.checkout(userId);
			response.setMessage("Checkout completed successfully");
			response.setStatus(HttpStatus.OK.value());
			response.setResponse(order);
			return ResponseEntity.ok(response);
		} catch (BookstoreException e) {
			log.error("Error :"+e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error("Error while checkout", e);
			throw new BookstoreException("Error while checkout", e);
		}
	}

	@Operation(description = "Get cart")
	@GetMapping(value = "getcart")
	public ResponseEntity<Response> getUserCart(@Valid @RequestParam Long userId) {
		try {
			AppResponse<List<ShoppingCartResponse>> response = new AppResponse<>();
			List<ShoppingCartResponse> cartList = shoppingCartService.getUserCart(userId);
			response.setMessage("Cart items retrieved successfully ");
			response.setStatus(HttpStatus.OK.value());
			response.setResponse(cartList);
			return ResponseEntity.ok(response);
		} catch (BookstoreException e) {
			log.error("Error :"+e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error("Error while Get cart", e);
			throw new BookstoreException("Error while Get cart", e);
		}
	}

	@Operation(description = "Remove book/s from cart")
	@DeleteMapping(value = "removecart")
	public ResponseEntity<Response> updateCart(@Valid @RequestBody ShoppingCartRequest shoppingCartRequest) {
		try {
			AppResponse<?> response = new AppResponse<>();
			String message = shoppingCartService.removeFromCart(shoppingCartRequest);
			response.setMessage("Item removed from cart successfully with message: " + message);
			response.setStatus(HttpStatus.OK.value());
			return ResponseEntity.ok(response);
		} catch (BookstoreException e) {
			log.error("Error :"+e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error("Error while Remove book/s from cart", e);
			throw new BookstoreException("Error while Remove book/s from cart", e);
		}
	}

	@Operation(description = "API to add books to cart")
	@PostMapping(value = "addcart")
	public ResponseEntity<Response> addCart(@Valid @RequestBody ShoppingCartRequest shoppingCartRequest) {
		try {
			AppResponse<ShoppingCartResponse> response = new AppResponse<>();
			ShoppingCartResponse cartResponse = shoppingCartService.addToCart(shoppingCartRequest);
			response.setMessage("Books added into cart successfully ");
			response.setStatus(HttpStatus.OK.value());
			response.setResponse(cartResponse);
			return ResponseEntity.ok(response);
		} catch (BookstoreException e) {
			log.error("Error :"+e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error("Error while add books to cart", e);
			throw new BookstoreException("Error while add books to cart", e);
		}
	}

	@ExceptionHandler(BookstoreException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	ResponseEntity<Response> handleResponseStatusException(BookstoreException ex) {
		Response response = AppUtility.getStoreExceptionResponse(ex);
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
