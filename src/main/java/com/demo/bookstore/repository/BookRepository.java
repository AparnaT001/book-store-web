package com.demo.bookstore.repository;

import com.demo.bookstore.entity.Book;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends CrudRepository<Book, Long> {
	
	List<Book> findAll();
	List<Book> findByCategoryId(Long id);

	Optional<Book> findById(Long id);

	Book save(Book book);

	Book findByTitle(String title);
}

