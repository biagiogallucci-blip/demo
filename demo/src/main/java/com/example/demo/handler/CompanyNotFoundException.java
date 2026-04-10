package com.example.demo.handler;

import java.math.BigInteger;

public class CompanyNotFoundException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public CompanyNotFoundException(BigInteger companyId) {
        super(String.format("Company con id %s non presente", companyId));
    }
}