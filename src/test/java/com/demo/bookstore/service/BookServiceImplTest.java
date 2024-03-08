package com.demo.bookstore.service;

import com.demo.bookstore.entity.Book;
import com.demo.bookstore.entity.Category;
import com.demo.bookstore.exception.BookstoreException;
import com.demo.bookstore.models.request.BookRequest;
import com.demo.bookstore.models.request.CategoryRequest;
import com.demo.bookstore.models.response.BookResponse;
import com.demo.bookstore.repository.BookRepository;
import com.demo.bookstore.repository.CategoryRepository;
import com.demo.bookstore.service.impl.BookServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetAllBooks() {
        // Mock data
        List<Book> books = new ArrayList<>();
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Book Title");
        book.setAuthor("Author");
        book.setCategoryId(1L);
        books.add(book);

        when(bookRepository.findAll()).thenReturn(books);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(new Category(1L, "Category")));

        // Invoke the method under test
        List<BookResponse> result = bookService.getAllBooks();

        // Verify result
        assertEquals(1, result.size());
        BookResponse response = result.get(0);
        assertEquals(Long.valueOf(1), response.getId());
        assertEquals("Book Title", response.getTitle());
        assertEquals("Author", response.getAuthor());
        assertEquals("Category", response.getCategory());

        // Verify interactions
        verify(bookRepository, times(1)).findAll();
        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetBooksByCategory() {
        // Mock data
        Category category = new Category(1L, "Category");
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Book Title");
        book.setAuthor("Author");
        book.setCategoryId(1L);
        List<Book> books = Collections.singletonList(book);

        when(categoryRepository.findByCategoryNameIgnoreCase("Category")).thenReturn(Optional.of(category));
        when(bookRepository.findByCategoryId(1L)).thenReturn(books);

        // Invoke the method under test
        List<BookResponse> result = bookService.getBooksByCategory("Category");

        // Verify result
        assertEquals(1, result.size());
        BookResponse response = result.get(0);
        assertEquals(Long.valueOf(1), response.getId());
        assertEquals("Book Title", response.getTitle());
        assertEquals("Author", response.getAuthor());
        assertEquals("Category", response.getCategory());

        // Verify interactions
        verify(categoryRepository, times(1)).findByCategoryNameIgnoreCase("Category");
        verify(bookRepository, times(1)).findByCategoryId(1L);
    }

    @Test
    public void testAddBook() {
        // Mock data
        BookRequest bookRequest = new BookRequest();
        bookRequest.setTitle("Book Title");
        bookRequest.setAuthor("Author");
        bookRequest.setCategory("Category");
        bookRequest.setPrice(10.0f);
        bookRequest.setStockQuantity(2L);

        Book book = new Book();
        book.setId(1L);
        book.setTitle("Book Title");
        book.setAuthor("Author");
        book.setCategoryId(1L);
        when(categoryRepository.findByCategoryNameIgnoreCase("Category")).thenReturn(Optional.of(new Category(1L, "Category")));
        when(bookRepository.save(any())).thenReturn(book);
        // Invoke the method under test
        List<BookResponse> result = bookService.addBook(Collections.singletonList(bookRequest));

        // Verify result
        assertEquals(1, result.size());
        BookResponse response = result.get(0);
        assertEquals("Book Title", response.getTitle());
        assertEquals("Author", response.getAuthor());
        assertEquals("Category", response.getCategory());

        // Verify interactions
        verify(categoryRepository, times(1)).findByCategoryNameIgnoreCase("Category");
        verify(bookRepository, times(1)).save(any());
    }

    @Test(expected = BookstoreException.class)
    public void testAddBookWithInvalidCategory() {
        // Mock data
        BookRequest bookRequest = new BookRequest();
        bookRequest.setCategory("Invalid Category");

        when(categoryRepository.findByCategoryNameIgnoreCase("Invalid Category")).thenReturn(Optional.empty());

        // Invoke the method under test
        bookService.addBook(Collections.singletonList(bookRequest));
    }

    @Test
    public void testRemoveBook() {
        // Mock data
        Book book = new Book();
        book.setId(1L);
        book.setAuthor("aut");
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        // Invoke the method under test
        String result = bookService.removeBook(1L);

        // Verify result
        assertEquals("Book removed successfully", result);

        // Verify interactions
        verify(bookRepository, times(1)).delete(book);
    }

    @Test(expected = BookstoreException.class)
    public void testRemoveBookNotFound() {
        // Mock data
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        // Invoke the method under test
        bookService.removeBook(1L);
    }

    @Test
    public void testGetCategories() {
        // Mock data
        List<Category> categories = Arrays.asList(new Category(1L, "Category1"), new Category(2L, "Category2"));
        when(categoryRepository.findAll()).thenReturn(categories);

        // Invoke the method under test
        List<Category> result = bookService.getCategories();

        // Verify result
        assertEquals(2, result.size());
        assertTrue(result.contains(new Category(1L, "Category1")));
        assertTrue(result.contains(new Category(2L, "Category2")));

        // Verify interactions
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    public void testAddCategories() {
        // Mock data
        List<CategoryRequest> categoryRequests = Arrays.asList(new CategoryRequest("Category1"), new CategoryRequest("Category2"));

        // Invoke the method under test
        String result = bookService.addCategories(categoryRequests);

        // Verify result
        assertEquals("Categories added successfully!!", result);

        // Verify interactions
        verify(categoryRepository, times(1)).findByCategoryNameIgnoreCase("Category1");
        verify(categoryRepository, times(1)).findByCategoryNameIgnoreCase("Category2");
        verify(categoryRepository, times(1)).saveAll(any());
    }

    @Test
    public void testAddCategoriesWithExistingCategory() {
        // Mock data
        List<CategoryRequest> categoryRequests = Collections.singletonList(new CategoryRequest("Category1"));
        when(categoryRepository.findByCategoryNameIgnoreCase("Category1")).thenReturn(Optional.of(new Category()));

        // Invoke the method under test
        String result = bookService.addCategories(categoryRequests);

        // Verify result
        assertEquals("Category mentioned already available!!", result);

        // Verify interactions
        verify(categoryRepository, times(1)).findByCategoryNameIgnoreCase("Category1");
        verify(categoryRepository, never()).saveAll(any());
    }
}

