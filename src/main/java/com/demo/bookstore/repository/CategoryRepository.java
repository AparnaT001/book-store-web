package com.demo.bookstore.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.demo.bookstore.entity.Category;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends CrudRepository<Category, Long>{
	
	Optional<Category> findByCategoryNameIgnoreCase(String category);
}
