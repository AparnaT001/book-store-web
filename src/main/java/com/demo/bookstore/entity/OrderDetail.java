package com.demo.bookstore.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "order_detail")
@Data
public class OrderDetail {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "order_detail_id")
	private Long orderDetailID;
	
	@ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
	
	@Column(name = "book_id")
	private Long bookId;

    @Column(name = "quantity")
    private Long quantity;

}