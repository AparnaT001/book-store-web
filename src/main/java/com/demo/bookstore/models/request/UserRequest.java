package com.demo.bookstore.models.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserRequest {

	@NotEmpty
	private String firstName;

	@NotEmpty
	private String lastName;

	@NotEmpty
	@Email(message ="INVALID_EMAIL")
	private String mailId;

	@NotEmpty
	@Schema(description = "Role name", allowableValues = "ADMIN,CUSTOMER")
	private String roleName;

	@Override
	public String toString() {
		return "UserRequest{" + "firstName='" + firstName + '\'' + ", lastName='" + lastName + '\'' + ", roleName='"
				+ roleName + '\'' + ", mailId='" + mailId + '\'' + '}';
	}
}
