package com.demo.bookstore.models.response;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class OrderResponse {

    private Long orderId;
    private String userName;
    private Float totalPrice;
    private LocalDateTime orderDate; 

}
