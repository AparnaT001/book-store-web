package com.demo.bookstore.models.response;

import lombok.Data;

@Data
public class UserResponse {
	
	private Long id;
	
	private String firstName;

	private String lastName;
	
	private String mailId;
	
	private Long roleId;

}
