package com.example.demo.request;

import java.math.BigInteger;

public class SaveCustomParametersForCompanyRequest {

	private BigInteger companyId;
	private String value;
	
	public BigInteger getCompanyId() {
		return companyId;
	}
	public void setCompanyId(BigInteger companyId) {
		this.companyId = companyId;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}