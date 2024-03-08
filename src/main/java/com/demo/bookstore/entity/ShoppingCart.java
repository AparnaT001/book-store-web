package com.demo.bookstore.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "shopping_cart")
@SequenceGenerator(name = "shoppingcartSequence", sequenceName = "cart_id_seq", allocationSize = 1)
@Data
public class ShoppingCart {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "shoppingcartSequence")
	@Column(name = "cart_id")
	private Long shoppingCartId;
	
	@Column(name = "user_id")
    private Long userId;
	
    @OneToMany(mappedBy = "shoppingCart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems = new ArrayList<>();

}