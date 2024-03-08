package com.demo.bookstore.models.response;

import java.util.HashMap;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "Standard API response structure")
public class Response {

	@Schema(description = "Status of the response")
    private Integer status;

	@Schema(description = "Response message to denote success or failure")
    private String message;
	
	@Schema(description = "Error messages are included as key value pair")
    private Map<String, String> errorMap = new HashMap<>();

    public Response(Integer status, String message) {
        this.status = status;
        this.message = message;
    }
}