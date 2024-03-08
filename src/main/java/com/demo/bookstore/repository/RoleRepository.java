package com.demo.bookstore.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.demo.bookstore.entity.Role;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends CrudRepository<Role, Long>{

	Optional<Role> findById(Long roleId);


	Optional<Role> findByRoleNameIgnoreCase(String roleName);
		
}
