package com.demo.bookstore.repository;

import com.demo.bookstore.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long>{

	Optional<User> findByMailId(String mailId);

	Optional<User> findById(Long userId);
		
}
