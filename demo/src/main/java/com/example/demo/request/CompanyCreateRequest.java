package com.example.demo.request;

import java.math.BigInteger;

public class CompanyCreateRequest {
	
	private BigInteger sourceCompanyId;

	public BigInteger getSourceCompanyId() {
		return sourceCompanyId;
	}

	public void setSourceCompanyId(BigInteger sourceCompanyId) {
		this.sourceCompanyId = sourceCompanyId;
	}
}