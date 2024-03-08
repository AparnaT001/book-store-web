package com.demo.bookstore.controller;

import com.demo.bookstore.models.request.ShoppingCartRequest;
import com.demo.bookstore.models.response.AppResponse;
import com.demo.bookstore.models.response.OrderResponse;
import com.demo.bookstore.models.response.Response;
import com.demo.bookstore.models.response.ShoppingCartResponse;
import com.demo.bookstore.service.ShoppingCartService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ShoppingCartControllerTest {

    @Mock
    private ShoppingCartService shoppingCartService;

    @InjectMocks
    private ShoppingCartController shoppingCartController;

    private ShoppingCartRequest validShoppingCartRequest;

    @Before
    public void setUp() {
        validShoppingCartRequest = new ShoppingCartRequest();
        validShoppingCartRequest.setBookId(123L);
        validShoppingCartRequest.setUserId(456L);
        validShoppingCartRequest.setQuantity(2L);
    }

    @Test
    public void testAddCart_Success() {
        // Mocking shoppingCartService behavior
        ShoppingCartResponse cartResponse = new ShoppingCartResponse();
        cartResponse.setBookName("Book 1");
        cartResponse.setUserId(456L);
        cartResponse.setQuantity(2L);
        cartResponse.setPrice(50.0f);

        when(shoppingCartService.addToCart(validShoppingCartRequest)).thenReturn(cartResponse);

        ResponseEntity<Response> responseEntity = shoppingCartController.addCart(validShoppingCartRequest);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Books added into cart successfully ", ((AppResponse<?>) responseEntity.getBody()).getMessage());
        assertEquals(cartResponse, ((AppResponse<?>) responseEntity.getBody()).getResponse());
    }

    @Test
    public void testUpdateCart_Success() {
        String message = "Item removed from cart successfully";
        when(shoppingCartService.removeFromCart(validShoppingCartRequest)).thenReturn(message);

        ResponseEntity<Response> responseEntity = shoppingCartController.updateCart(validShoppingCartRequest);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Item removed from cart successfully with message: " + message,
                ((AppResponse<?>) responseEntity.getBody()).getMessage());
    }

    @Test
    public void testGetUserCart_Success() {
        // Mocking shoppingCartService behavior
        List<ShoppingCartResponse> cartList = new ArrayList<>();
        ShoppingCartResponse cartResponse = new ShoppingCartResponse();
        cartResponse.setBookName("Book 1");
        cartResponse.setUserId(456L);
        cartResponse.setQuantity(2L);
        cartResponse.setPrice(50.0f);
        cartList.add(cartResponse);

        when(shoppingCartService.getUserCart(validShoppingCartRequest.getUserId())).thenReturn(cartList);

        ResponseEntity<Response> responseEntity = shoppingCartController.getUserCart(validShoppingCartRequest.getUserId());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Cart items retrieved successfully ", ((AppResponse<?>) responseEntity.getBody()).getMessage());
        assertEquals(cartList, ((AppResponse<?>) responseEntity.getBody()).getResponse());
    }

    @Test
    public void testCheckoutBooks_Success() {
        // Mocking shoppingCartService behavior
        OrderResponse order = new OrderResponse();
        order.setOrderId(789L);
        order.setUserName("John Doe");
        order.setTotalPrice(100.0f);

        when(shoppingCartService.checkout(validShoppingCartRequest.getUserId())).thenReturn(order);

        ResponseEntity<Response> responseEntity = shoppingCartController.checkoutBooks(validShoppingCartRequest.getUserId());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Checkout completed successfully", ((AppResponse<?>) responseEntity.getBody()).getMessage());
        assertEquals(order, ((AppResponse<?>) responseEntity.getBody()).getResponse());
    }
}
