package com.example.demo.handler;

import java.math.BigInteger;

public class CompanyParametersAlreadyExistException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public CompanyParametersAlreadyExistException(BigInteger companyId, BigInteger paramId) {
        super(String.format("Associazione già esistente per company id %s e param id %s", companyId, paramId));
    }
}