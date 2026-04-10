package com.example.demo.handler;

import java.math.BigInteger;

public class CategoriesNotFoundException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public CategoriesNotFoundException(BigInteger ruleId) {
        super(String.format("Regola di esclusione con id %s non presente", ruleId));
    }
}