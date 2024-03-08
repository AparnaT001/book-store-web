package com.demo.bookstore.models.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Schema(description =  "API response with result")
public class AppResponse<T> extends Response {

	@Schema(description = "response")
	private T response;

    public AppResponse(Integer status, String message) {
        super(status,message);
    }
}