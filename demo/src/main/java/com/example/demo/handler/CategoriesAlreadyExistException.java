package com.example.demo.handler;

public class CategoriesAlreadyExistException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public CategoriesAlreadyExistException(String tag) {
        super(String.format("Regola di esclusione con code %s già presente", tag));
    }
}