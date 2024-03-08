package com.demo.bookstore.exception;

public class BookstoreException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1223L;

	public BookstoreException(String message) {
        super(message);
    }

	public BookstoreException(String message, Exception e) {
		 super(message,e);
	}
}
