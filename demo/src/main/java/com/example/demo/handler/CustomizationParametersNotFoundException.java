package com.example.demo.handler;

import java.math.BigInteger;

public class CustomizationParametersNotFoundException extends RuntimeException  {

private static final long serialVersionUID = 1L;
	
	public CustomizationParametersNotFoundException(String parameterCode) {
        super(String.format("Parametro custom con codice %s non presente", parameterCode));
    }
	
	public CustomizationParametersNotFoundException(BigInteger paramId) {
        super(String.format("Parametro custom con id %s non presente", paramId));
    }
}