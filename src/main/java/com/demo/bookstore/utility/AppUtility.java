package com.demo.bookstore.utility;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import com.demo.bookstore.exception.BookstoreException;
import com.demo.bookstore.models.response.Response;

public class AppUtility {

	
    public static Response getStoreExceptionResponse(BookstoreException ex){
    	Response response = new Response();
        response.setStatus(500);
        response.setMessage(ex.getMessage());
        return response;
    }
    
	public static void setErrorMap(BindingResult bindingResult, Response response) {
		response.setStatus(HttpStatus.BAD_REQUEST.value());
		List<FieldError> l = bindingResult.getFieldErrors();
		for (FieldError e : l) {
			response.getErrorMap().put(e.getField(), e.getCode());
		}
	}
}
