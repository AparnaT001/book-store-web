package com.demo.bookstore.models.response;

import lombok.Data;

@Data
public class BookResponse {

    private Long id;

    private String title;

    private String author;

    private Long categoryId;
    
    private String category;

	private Float price;

    private Long stockQuantity;


}
