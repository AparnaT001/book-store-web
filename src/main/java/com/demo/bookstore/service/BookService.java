package com.demo.bookstore.service;

import java.util.List;

import javax.validation.Valid;

import com.demo.bookstore.entity.Category;
import com.demo.bookstore.models.request.BookRequest;
import com.demo.bookstore.models.request.CategoryRequest;
import com.demo.bookstore.models.response.BookResponse;

public interface BookService {

	List<BookResponse> getAllBooks();

	List<BookResponse> getBooksByCategory(String category);

	List<BookResponse> addBook(List<BookRequest> book);

	String removeBook(@Valid Long id);

	List<Category> getCategories();

	String addCategories(List<CategoryRequest> categories);

}
