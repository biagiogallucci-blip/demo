package com.example.demo.handler;

public class CustomParametersAlreadyExistException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public CustomParametersAlreadyExistException(String code) {
        super(String.format("Parametro custom con code %s già presente", code));
    }
}