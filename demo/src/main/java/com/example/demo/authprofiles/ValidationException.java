package com.example.demo.authprofiles;

import java.util.List;

public class ValidationException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	private final List<ErrorDetail> details;

    public ValidationException(List<ErrorDetail> details) {
        super("Utente non creato");
        this.details = details;
    }

    public List<ErrorDetail> getDetails() {
        return details;
    }
}