package com.demo.bookstore.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.demo.bookstore.config.SwaggerConfig;
import com.demo.bookstore.entity.Category;
import com.demo.bookstore.exception.BookstoreException;
import com.demo.bookstore.models.request.BookRequest;
import com.demo.bookstore.models.request.CategoryRequest;
import com.demo.bookstore.models.response.AppResponse;
import com.demo.bookstore.models.response.BookResponse;
import com.demo.bookstore.models.response.Response;
import com.demo.bookstore.service.BookService;
import com.demo.bookstore.service.impl.AuthenticationService;
import com.demo.bookstore.utility.AppUtility;
import com.demo.bookstore.utility.Constants;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Aparna Thirumoorthy
 *
 */

@RestController("book/")
@Tag(name = SwaggerConfig.BOOK_TAG)
@Slf4j
public class BookStoreController {

	@Autowired
	private BookService bookService;

	@Autowired
	private AuthenticationService authenticationService;

	@Operation(description = "Add book to store")
	@PostMapping(value = "add")
	public ResponseEntity<Response> createBook(
			@RequestHeader(value = Constants.USER_HEADER, required = true) String userId,
			@Valid @RequestBody List<BookRequest> booksToAdd,BindingResult bindingResult) {
		try {
			AppResponse<List<BookResponse>> response = new AppResponse<>();
			if (bindingResult.hasErrors()) {
				AppResponse<?> res = new AppResponse<>(HttpStatus.BAD_REQUEST.value(), Constants.INVALID_PAYLOAD);
				AppUtility.setErrorMap(bindingResult, res);
				return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
			}
			authenticationService.authenticate(userId);
			List<BookResponse> booksAdded = new ArrayList<>();
			booksAdded = bookService.addBook(booksToAdd);
			response.setMessage("Book created successfully");
			response.setResponse(booksAdded);
			response.setStatus(HttpStatus.OK.value());
			return ResponseEntity.ok(response);
		} catch (BookstoreException e) {
			log.error("Error :"+e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error("Error while creating book", e);
			throw new BookstoreException("Error while creating book", e);
		}
	}

	@Operation(description = "Remove book from store")
	@DeleteMapping(value = "remove/{id}")
	public ResponseEntity<Response> removeBook(
			@RequestHeader(value = Constants.USER_HEADER, required = true) String userId,
			@NotNull @RequestParam Long id) {
		try {
			authenticationService.authenticate(userId);
			AppResponse<?> response = new AppResponse<>();
			String msg = bookService.removeBook(id);
			response.setMessage("Book :" + msg + " removed successfully ");
			response.setStatus(HttpStatus.OK.value());
			return ResponseEntity.ok(response);
		} catch (BookstoreException e) {
			log.error("Error :"+e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error("Error while removing book", e);
			throw new BookstoreException("Error while removing book", e);
		}
	}

	@Operation(description = "Get books available in store by category")
	@GetMapping(value = "list")
	public ResponseEntity<Response> getBooks(@NotNull @Valid @RequestParam String category) {

		try {
			AppResponse<List<BookResponse>> response = new AppResponse<>();
			List<BookResponse> books = bookService.getBooksByCategory(category);
			response.setMessage("Available books retrived successfully ");
			response.setStatus(HttpStatus.OK.value());
			response.setResponse(books);
			return ResponseEntity.ok(response);
		} catch (BookstoreException e) {
			log.error("Error :"+e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error("Error while get books", e);
			throw new BookstoreException("Error while get books", e);
		}
	}

	@Operation(description = "Get book categories")
	@GetMapping(value = "listCategories")
	public ResponseEntity<Response> listCategories() {
		try {
			AppResponse<List<Category>> response = new AppResponse<>();
			List<Category> categories = bookService.getCategories();
			response.setMessage("Book categories retrived successfully ");
			response.setStatus(HttpStatus.OK.value());
			response.setResponse(categories);
			return ResponseEntity.ok(response);
		} catch (BookstoreException e) {
			log.error("Error :"+e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error("Error while get book categories", e);
			throw new BookstoreException("Error while get book categories", e);
		}
	}

	@Operation(description = "Add new book categories")
	@PostMapping(value = "addCategories")
	public ResponseEntity<Response> addCategories(
			@RequestHeader(value = Constants.USER_HEADER, required = true) String userId,
			@RequestBody List<CategoryRequest> categoryRequest,BindingResult bindingResult) {
		try {
			AppResponse<?> response = new AppResponse<>();
			if (bindingResult.hasErrors()) {
				response = new AppResponse<>(HttpStatus.BAD_REQUEST.value(), Constants.INVALID_PAYLOAD);
				AppUtility.setErrorMap(bindingResult, response);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}
			authenticationService.authenticate(userId);
			response.setMessage(bookService.addCategories(categoryRequest));
			response.setStatus(HttpStatus.OK.value());
			return ResponseEntity.ok(response);
		} catch (BookstoreException e) {
			log.error("Error :"+e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error("Error while add book categories", e);
			throw new BookstoreException("Error while add book categories", e);
		}
	}

	@ExceptionHandler(BookstoreException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	ResponseEntity<Response> handleResponseStatusException(BookstoreException ex) {
		Response response = AppUtility.getStoreExceptionResponse(ex);
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
