package com.example.demo.handler;

import java.math.BigInteger;

public class CompanyParametersPreviewNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public CompanyParametersPreviewNotFoundException(BigInteger paramId) {
        super(String.format("Parametro custom con id %s non presente", paramId));
    }
}