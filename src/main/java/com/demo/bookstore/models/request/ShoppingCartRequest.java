package com.demo.bookstore.models.request;

import lombok.Data;


@Data
public class ShoppingCartRequest {

	private Long userId;

	private Long bookId;

	private Long quantity;

	@Override
	public String toString() {
		return "ShoppingCartRequest{" +
				"userId=" + userId +
				", bookId=" + bookId +
				", quantity=" + quantity +
				'}';
	}



}
