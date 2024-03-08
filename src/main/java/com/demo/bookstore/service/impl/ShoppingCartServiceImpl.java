package com.demo.bookstore.service.impl;

import com.demo.bookstore.entity.*;
import com.demo.bookstore.exception.BookstoreException;
import com.demo.bookstore.models.request.ShoppingCartRequest;
import com.demo.bookstore.models.response.OrderResponse;
import com.demo.bookstore.models.response.ShoppingCartResponse;
import com.demo.bookstore.repository.BookRepository;
import com.demo.bookstore.repository.OrderRepository;
import com.demo.bookstore.repository.ShoppingCartRepository;
import com.demo.bookstore.repository.UserRepository;
import com.demo.bookstore.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {

    public static final String UNABLE_TO_REMOVE_ITEM_FROM_CART = "Unable to remove item from cart ";
    public static final String CART_UPDATED_SUCCESSFULLY = "Cart updated Successfully";
    private static final int MAX_RETRIES = 3;
    private static final long INITIAL_DELAY_MS = 100; // Initial delay in milliseconds
    private static final long MAX_DELAY_MS = 3000;    // Maximum delay in milliseconds

    @Autowired
    ShoppingCartRepository shoppingCartRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    BookRepository bookRepository;

    @Autowired
    UserRepository userRepository;

    @Override
    public ShoppingCartResponse addToCart(ShoppingCartRequest request) {
    	 // Retrieve the book
        Optional<Book> optionalBook = bookRepository.findById(request.getBookId());
        if (!optionalBook.isPresent()) {
            throw new BookstoreException("Book not found");
        }

        Book book = optionalBook.get();

        List<ShoppingCartResponse> userCart = getUserCart(request.getUserId());
        List<ShoppingCartResponse> bookAlreadyInCart = userCart.
                stream()
                .filter(b -> b.getBookName() == book.getTitle())
                .collect(Collectors.toList());
        // Check if the requested quantity is available in stock
        // along with existing cart details
        if(bookAlreadyInCart.size() >0){
            request.setQuantity(bookAlreadyInCart.get(0).getQuantity() + request.getQuantity());
        }
        // Check if the requested quantity is available in stock
        if (book.getStockQuantity() < request.getQuantity()) {
            throw new BookstoreException("Insufficient stock quantity");
        }

        // Retrieve or create shopping cart
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(request.getUserId());
        if (shoppingCart == null) {
            shoppingCart = new ShoppingCart();
            shoppingCart.setUserId(request.getUserId());
        }

        // Check if the book already exists in the shopping cart
        Optional<CartItem> existingCartItem = shoppingCart.getCartItems().stream()
                                                .filter(cartItem -> cartItem.getBook().getId().equals(book.getId()))
                                                .findFirst();

        if (existingCartItem.isPresent()) {
            // Update existing cart item
            CartItem cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity()); // Increase quantity
            cartItem.setPrice(cartItem.getPrice() + (book.getPrice() * request.getQuantity())); // Update price
        } else {
            // Create a new cart item
            CartItem cartItem = new CartItem();
            cartItem.setBook(book);
            cartItem.setQuantity(request.getQuantity());
            cartItem.setPrice(book.getPrice() * request.getQuantity());
            cartItem.setShoppingCart(shoppingCart);
            shoppingCart.getCartItems().add(cartItem);
        }

        // Save or update the shopping cart
        shoppingCart = shoppingCartRepository.save(shoppingCart);

        // Prepare and return the ShoppingCartResponse
        ShoppingCartResponse response = new ShoppingCartResponse();
        response.setUserId(shoppingCart.getUserId());
        response.setBookName(book.getTitle());
        response.setQuantity(request.getQuantity());
        response.setPrice(book.getPrice() * request.getQuantity());
        return response;
    }

    @Override
    @Transactional
    public String removeFromCart(ShoppingCartRequest request) {
        try {
            // Retrieve the book
            Optional<Book> optionalBook = bookRepository.findById(request.getBookId());
            if (!optionalBook.isPresent()) {
                throw new BookstoreException("Book not found");
            }

            Book book = optionalBook.get();

            // Retrieve the shopping cart
            ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(request.getUserId());
            if (shoppingCart == null) {
                throw new BookstoreException("Shopping cart not found");
            }
            // Remove the cart item if entire quantity is removed
            shoppingCart.getCartItems().forEach(cartItem -> {
                if(cartItem.getBook().equals(book)  && cartItem.getQuantity() > request.getQuantity()){
                    cartItem.setQuantity(cartItem.getQuantity() - request.getQuantity());
                } else {
                    shoppingCart.getCartItems().remove(cartItem);
                }
            });
            shoppingCartRepository.save(shoppingCart);
            return CART_UPDATED_SUCCESSFULLY;
        } catch (Exception e) {
            throw new BookstoreException(UNABLE_TO_REMOVE_ITEM_FROM_CART, e);
        }
    }

    @Override
    public List<ShoppingCartResponse> getUserCart(Long userId) {
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(userId);
        if (shoppingCart == null) {
            throw new BookstoreException("Shopping cart not found for user with ID: " + userId);
        }

        List<ShoppingCartResponse> cartResponses = new ArrayList<>();
        for (CartItem cartItem : shoppingCart.getCartItems()) {
            ShoppingCartResponse response = new ShoppingCartResponse();
            BeanUtils.copyProperties(cartItem, response);
            response.setUserId(userId);
            response.setBookName(cartItem.getBook().getTitle());
            cartResponses.add(response);
        }

        return cartResponses;
    }

    @Override
    @Transactional
    public OrderResponse checkout(Long userId) {
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(userId);
        if (shoppingCart == null || shoppingCart.getCartItems().isEmpty()) {
            throw new BookstoreException("Shopping cart is empty for user with ID: " + userId);
        }

        Order order = createOrder(userId, shoppingCart);
        log.info("Order created successfully");
        saveOrderWithRetry(order);
        log.info("Order saved successfully");
        updateBookQuantities(shoppingCart); // Reduce book quantities
        log.info("Book quantities updated successfully");
        clearShoppingCart(shoppingCart);
        log.info("Shopping cart cleared successfully");
        return prepareOrderResponse(order);
    }

    private Order createOrder(Long userId, ShoppingCart shoppingCart) {
        Order order = new Order();
        order.setUserId(userId);
        order.setOrderDate(LocalDateTime.now());

        List<OrderDetail> orderDetails = new ArrayList<>();
        float totalPrice = 0;

        for (CartItem cartItem : shoppingCart.getCartItems()) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setBookId(cartItem.getBook().getId());
            orderDetail.setQuantity(cartItem.getQuantity());
            orderDetails.add(orderDetail);

            totalPrice += cartItem.getPrice();
        }

        order.setOrderDetailsList(orderDetails);
        order.setTotalPrice(totalPrice);

        return order;
    }
    
    private synchronized void updateBookQuantities(ShoppingCart shoppingCart) {
        for (CartItem cartItem : shoppingCart.getCartItems()) {
            Long bookId = cartItem.getBook().getId();
            Book book = bookRepository.findById(bookId)
                                      .orElseThrow(() -> new BookstoreException("Book not found with ID: " + bookId));
            long purchasedQuantity = cartItem.getQuantity();
            long remainingQuantity = book.getStockQuantity() - purchasedQuantity;
            if (remainingQuantity < 0) {
                throw new BookstoreException("Insufficient stock quantity for book with ID: " + bookId);
            }
            book.setStockQuantity(remainingQuantity);
            bookRepository.save(book);
        }
    }

    private void saveOrderWithRetry(Order order) {
        int retryCount = 0;
        while (true) {
            try {
                orderRepository.save(order);
                return; // Success, exit the loop
            } catch (OptimisticLockingFailureException ex) {
                if (retryCount >= MAX_RETRIES) {
                    throw new BookstoreException("Failed to save order due to concurrency issue after multiple retries");
                }
                retryCount++;
                long delay = Math.min(INITIAL_DELAY_MS * (1 << retryCount), MAX_DELAY_MS); // Exponential backoff
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new BookstoreException("Thread interrupted while waiting for retry");
                }
            }
        }
    }

    private void clearShoppingCart(ShoppingCart shoppingCart) {
        shoppingCart.getCartItems().clear();
        shoppingCartRepository.save(shoppingCart);
    }

    private OrderResponse prepareOrderResponse(Order order) {
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setOrderId(order.getId());
        Optional<User> optionalUser=userRepository.findById(order.getUserId());
        orderResponse.setUserName(optionalUser.isPresent()?optionalUser.get().getFirstName():"");
        orderResponse.setOrderDate(order.getOrderDate());
        orderResponse.setTotalPrice(order.getTotalPrice());
        return orderResponse;
    }
}
