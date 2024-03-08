package com.demo.bookstore.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "book")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
    private Long id;
	
	@Column(name = "title")
    private String title;
    
	@Column(name = "author")
    private String author;
	
	@Column(name = "category_id")
    private Long categoryId;
	
	@Column(name = "price")
    private Float price;
    
	@Column(name = "stock_quantity")
    private Long stockQuantity;
    
	@Override
	public String toString() {
		return "Book [id=" + id + ", title=" + title + ", author=" + author + ", categoryId=" + categoryId + ", price="
				+ price + ", stockQuantity=" + stockQuantity + "]";
	}

}
