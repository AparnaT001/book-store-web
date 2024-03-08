package com.demo.bookstore.models.request;


import java.util.List;

import lombok.Data;

@Data
public class OrderRequest {

    private UserRequest user;
    private List<BookRequest> books;

}