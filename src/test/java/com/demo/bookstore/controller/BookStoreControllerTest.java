package com.demo.bookstore.controller;

import com.demo.bookstore.exception.BookstoreException;
import com.demo.bookstore.models.request.BookRequest;
import com.demo.bookstore.models.response.BookResponse;
import com.demo.bookstore.models.response.Response;
import com.demo.bookstore.service.BookService;
import com.demo.bookstore.service.impl.AuthenticationService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BookStoreControllerTest {

    @Mock
    private BookService bookService;

    @Mock
    BindingResult bindingResult;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private BookStoreController bookStoreController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateBook_Success() {
        List<BookRequest> booksToAdd = new ArrayList<>();
        BookRequest bookRequest = new BookRequest();
        booksToAdd.add(bookRequest);

        List<BookResponse> booksAdded = new ArrayList<>();
        booksAdded.add(new BookResponse());

        when(bindingResult.hasErrors()).thenReturn(false);
        when(bookService.addBook(anyList())).thenReturn(booksAdded);

        ResponseEntity<Response> result = bookStoreController.createBook("userId", booksToAdd, bindingResult);

        Assert.assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(authenticationService, times(1)).authenticate("userId");
        verify(bookService, times(1)).addBook(booksToAdd);
    }

    @Test
    public void testCreateBook_WithInvalidPayload() {
        List<BookRequest> booksToAdd = new ArrayList<>();
        BookRequest bookRequest = new BookRequest();
        booksToAdd.add(bookRequest);

        when(bindingResult.hasErrors()).thenReturn(true);

        ResponseEntity<Response> result = bookStoreController.createBook("userId", booksToAdd, bindingResult);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        verifyNoInteractions(authenticationService);
        verifyNoInteractions(bookService);
    }

    @Test(expected = BookstoreException.class)
    public void testCreateBook_ThrowsException() {
        List<BookRequest> booksToAdd = new ArrayList<>();
        BookRequest bookRequest = new BookRequest();
        booksToAdd.add(bookRequest);

        when(bindingResult.hasErrors()).thenReturn(false);
        when(bookService.addBook(any())).thenThrow(new BookstoreException("issue"));

        ResponseEntity<Response> result = bookStoreController.createBook("userId", booksToAdd, bindingResult);

        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        verify(authenticationService, times(1)).authenticate("userId");
        verify(bookService, times(1)).addBook(booksToAdd);
    }
}
