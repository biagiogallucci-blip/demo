package com.example.demo.handler;

import java.math.BigInteger;

public class CompanyCategoriesNotFoundException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public CompanyCategoriesNotFoundException(BigInteger ruleId) {
        super(String.format("Regola di esclusione con id %s non presente", ruleId));
    }
}