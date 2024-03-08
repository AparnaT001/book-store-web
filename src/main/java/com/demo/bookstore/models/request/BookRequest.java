package com.demo.bookstore.models.request;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class BookRequest {

	@NotEmpty
	private String title;
	@NotEmpty
	private String author;
	@NotEmpty
	private String category;
	@NotNull
	private Float price;
	@NotNull
	@Min(1)
	private Long stockQuantity;

	@Override
	public String toString() {
		return "Book [title=" + title + ", author=" + author + ", category=" + category + ", price=" + price
				+ ", stockQuantity=" + stockQuantity + "]";
	}

}
