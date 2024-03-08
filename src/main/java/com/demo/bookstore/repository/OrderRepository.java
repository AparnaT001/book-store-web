package com.demo.bookstore.repository;

import org.springframework.data.repository.CrudRepository;

import com.demo.bookstore.entity.Order;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends CrudRepository<Order, Long> {
}