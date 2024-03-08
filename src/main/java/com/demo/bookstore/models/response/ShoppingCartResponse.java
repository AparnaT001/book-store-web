package com.demo.bookstore.models.response;

import lombok.Data;

@Data
public class ShoppingCartResponse {

	private Long userId;

	private String bookName;

	private Long quantity;

	private Float price;

	@Override
	public String toString() {
		return "ShoppingCartRequest{" +
				"userId=" + userId +
				", bookName=" + bookName +
				", quantity=" + quantity +
				", price=" + price +
				'}';
	}


}
