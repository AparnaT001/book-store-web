package com.demo.bookstore.service.impl;

import com.demo.bookstore.entity.Book;
import com.demo.bookstore.entity.Category;
import com.demo.bookstore.exception.BookstoreException;
import com.demo.bookstore.models.request.BookRequest;
import com.demo.bookstore.models.request.CategoryRequest;
import com.demo.bookstore.models.response.BookResponse;
import com.demo.bookstore.repository.BookRepository;
import com.demo.bookstore.repository.CategoryRepository;
import com.demo.bookstore.service.BookService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class BookServiceImpl implements BookService {

    public static final String BOOK_REMOVED_SUCCESSFULLY = "Book removed successfully";

    public static final String BOOK_NOT_FOUND = "Book not found with id: ";
    public static final String ADD_THE_BOOK_CATEGORY = "Category mentioned not available !! Kindly add the book category ";
    public static final String CATEGORIES_ADDED_SUCCESSFULLY = "Categories added successfully!!";
    public static final String CATEGORY_MENTIONED_ALREADY_AVAILABLE = "Category mentioned already available!!";

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<BookResponse> getAllBooks() {
        List<BookResponse> bookResponses = new ArrayList<>();
        List<Book> books = bookRepository.findAll();
        for (Book book : books) {
            BookResponse bookResponse = new BookResponse();
            BeanUtils.copyProperties(book, bookResponse);
            bookResponse.setCategory(categoryRepository.findById(book.getCategoryId()).get().getCategoryName());
            bookResponses.add(bookResponse);
        }
        return bookResponses;
    }

    @Override
    public List<BookResponse> getBooksByCategory(String categoryName) {
        List<BookResponse> bookResponses = new ArrayList<>();
        Optional<Category> category = categoryRepository.findByCategoryNameIgnoreCase(categoryName);
        if (category.isPresent()) {
            List<Book> books = bookRepository.findByCategoryId(category.get().getId());
            for (Book book : books) {
                BookResponse bookResponse = new BookResponse();
                BeanUtils.copyProperties(book, bookResponse);
                bookResponse.setCategory(categoryName);
                bookResponses.add(bookResponse);
            }
        }
        return bookResponses;
    }

    @Override
    public  List<BookResponse> addBook(List<BookRequest> books) {
        List<BookResponse> bookResponses = new ArrayList<>();
        for (BookRequest bookRequest : books) {
            Book bookPo = new Book();
            Book book = bookRepository.findByTitle(bookRequest.getTitle());
            //If same book added just update the quantity of book
            if(book != null && book.getId() > 0){
                bookRequest.setStockQuantity(book.getStockQuantity() + bookRequest.getStockQuantity());
                bookPo.setId(book.getId());
            }
            Optional<Category> category = categoryRepository.findByCategoryNameIgnoreCase(bookRequest.getCategory());
            if (category.isPresent()) {
                BeanUtils.copyProperties(bookRequest, bookPo);
                bookPo.setCategoryId(category.get().getId());
                bookPo=bookRepository.save(bookPo);
                BookResponse bookResponse = new BookResponse();
                BeanUtils.copyProperties(bookPo, bookResponse);
                bookResponse.setCategory(category.get().getCategoryName());
                bookResponses.add(bookResponse);
            } else {
                throw new BookstoreException(ADD_THE_BOOK_CATEGORY);
            }
        }
        return bookResponses;
    }

    @Override
    public String removeBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookstoreException(BOOK_NOT_FOUND + id));
        bookRepository.delete(book);
        return BOOK_REMOVED_SUCCESSFULLY;
    }

    @Override
    public List<Category> getCategories() {
        List<Category> categories = new ArrayList<>();
        categoryRepository.findAll()
                .iterator()
                .forEachRemaining(categories::add);
        return categories;
    }

    @Override
    public String addCategories(List<CategoryRequest> categoriesRequest) {
        List<Category> categories = new ArrayList<>();
        AtomicReference<Boolean> isCategoryAvailable = new AtomicReference<>(false);
        categoriesRequest.forEach(req -> {
            Optional<Category> category = categoryRepository.findByCategoryNameIgnoreCase(req.getCategoryName());
            if(!category.isPresent()) {
                Category categoryAdd = new Category();
                categoryAdd.setCategoryName(req.getCategoryName());
                categories.add(categoryAdd);
            } else{
                isCategoryAvailable.set(true);
            }
        });
        if(isCategoryAvailable.get())
            return CATEGORY_MENTIONED_ALREADY_AVAILABLE;
        categoryRepository.saveAll(categories);
        return CATEGORIES_ADDED_SUCCESSFULLY;
    }

}
