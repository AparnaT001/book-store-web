package com.demo.bookstore.service;

import com.demo.bookstore.entity.Book;
import com.demo.bookstore.entity.CartItem;
import com.demo.bookstore.entity.Order;
import com.demo.bookstore.entity.ShoppingCart;
import com.demo.bookstore.exception.BookstoreException;
import com.demo.bookstore.models.request.ShoppingCartRequest;
import com.demo.bookstore.models.response.OrderResponse;
import com.demo.bookstore.models.response.ShoppingCartResponse;
import com.demo.bookstore.repository.BookRepository;
import com.demo.bookstore.repository.OrderRepository;
import com.demo.bookstore.repository.ShoppingCartRepository;
import com.demo.bookstore.repository.UserRepository;
import com.demo.bookstore.service.impl.ShoppingCartServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ShoppingCartServiceImplTest  {

    @Mock
    private ShoppingCartRepository shoppingCartRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAddToCart() {
        ShoppingCartRequest request = new ShoppingCartRequest();
        request.setUserId(1L);
        request.setBookId(1L);
        request.setQuantity(2L);

        Book book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setPrice(10.0f);
        book.setStockQuantity(5L);

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(1L);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(shoppingCartRepository.findByUserId(1L)).thenReturn(shoppingCart);
        when(shoppingCartRepository.save(any(ShoppingCart.class))).thenReturn(shoppingCart);

        ShoppingCartResponse response = shoppingCartService.addToCart(request);

        assertNotNull(response);
        assertEquals(1L, response.getUserId().longValue());
        assertEquals("Test Book", response.getBookName());
        assertEquals(2L, response.getQuantity().longValue());
        assertEquals(20.0, response.getPrice(), 0.01);

        verify(bookRepository, times(1)).findById(1L);
        verify(shoppingCartRepository, times(2)).findByUserId(1L);
        verify(shoppingCartRepository, times(1)).save(any(ShoppingCart.class));
    }

    @Test(expected = BookstoreException.class)
    public void testAddToCartWithInvalidBook() {
        ShoppingCartRequest request = new ShoppingCartRequest();
        request.setUserId(1L);
        request.setBookId(1L);

        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        shoppingCartService.addToCart(request);
    }

    @Test
    public void testRemoveFromCart() {
        ShoppingCartRequest request = new ShoppingCartRequest();
        request.setUserId(1L);
        request.setBookId(1L);
        request.setQuantity(5L);

        Book book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setPrice(10.0f);
        book.setStockQuantity(5L);

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(1L);
        CartItem cartItem = new CartItem();
        cartItem.setBook(book);
        cartItem.setQuantity(10L);
        shoppingCart.getCartItems().add(cartItem);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(shoppingCartRepository.findByUserId(1L)).thenReturn(shoppingCart);
        when(shoppingCartRepository.save(any(ShoppingCart.class))).thenReturn(shoppingCart);

        String result = shoppingCartService.removeFromCart(request);

        assertEquals(ShoppingCartServiceImpl.CART_UPDATED_SUCCESSFULLY, result);

        verify(bookRepository, times(1)).findById(1L);
        verify(shoppingCartRepository, times(1)).findByUserId(1L);
        verify(shoppingCartRepository, times(1)).save(any(ShoppingCart.class));
    }

    @Test(expected = BookstoreException.class)
    public void testRemoveFromCartWithInvalidBook() {
        ShoppingCartRequest request = new ShoppingCartRequest();
        request.setUserId(1L);
        request.setBookId(1L);

        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        shoppingCartService.removeFromCart(request);
    }

    @Test
    public void testGetUserCart() {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(1L);
        CartItem cartItem = new CartItem();
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        cartItem.setBook(book);
        shoppingCart.getCartItems().add(cartItem);

        when(shoppingCartRepository.findByUserId(1L)).thenReturn(shoppingCart);

        List<ShoppingCartResponse> responses = shoppingCartService.getUserCart(1L);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        ShoppingCartResponse response = responses.get(0);
        assertEquals(1L, response.getUserId().longValue());
        assertEquals("Test Book", response.getBookName());

        verify(shoppingCartRepository, times(1)).findByUserId(1L);
    }

    @Test(expected = BookstoreException.class)
    public void testGetUserCartWithEmptyCart() {
        when(shoppingCartRepository.findByUserId(1L)).thenReturn(null);

        shoppingCartService.getUserCart(1L);
    }

    @Test
    public void testCheckout() {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(1L);
        CartItem cartItem = new CartItem();
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setPrice(10.0f);
        book.setStockQuantity(5L);
        cartItem.setBook(book);
        cartItem.setQuantity(2L);
        cartItem.setPrice(10.0f);
        shoppingCart.getCartItems().add(cartItem);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(shoppingCartRepository.findByUserId(1L)).thenReturn(shoppingCart);
        when(orderRepository.save(any(Order.class))).thenReturn(new Order());

        OrderResponse orderResponse = shoppingCartService.checkout(1L);

        assertNotNull(orderResponse);
        assertEquals(10.0, orderResponse.getTotalPrice(), 0.01);

        verify(shoppingCartRepository, times(1)).findByUserId(1L);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test(expected = BookstoreException.class)
    public void testCheckoutWithEmptyCart() {
        when(shoppingCartRepository.findByUserId(1L)).thenReturn(null);

        shoppingCartService.checkout(1L);
    }
}
