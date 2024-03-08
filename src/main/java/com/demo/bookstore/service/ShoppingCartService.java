package com.demo.bookstore.service;

import java.util.List;

import com.demo.bookstore.models.request.ShoppingCartRequest;
import com.demo.bookstore.models.response.OrderResponse;
import com.demo.bookstore.models.response.ShoppingCartResponse;


public interface ShoppingCartService {
	
	ShoppingCartResponse  addToCart(ShoppingCartRequest cartAddRequest);
	
	String removeFromCart(ShoppingCartRequest cartRemoveRequest);
	
	List<ShoppingCartResponse> getUserCart(Long userId);

	OrderResponse checkout(Long userId);

}