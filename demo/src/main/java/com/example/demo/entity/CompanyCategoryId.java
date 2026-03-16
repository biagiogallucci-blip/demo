package com.example.demo.entity;

import java.io.Serializable;
import java.math.BigInteger;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class CompanyCategoryId implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "COMPANY_ID")
    private BigInteger companyId;

    @Column(name = "CATEGORY_CODE", length = 50)
    private String categoryCode;

	public BigInteger getCompanyId() {
		return companyId;
	}

	public void setCompanyId(BigInteger companyId) {
		this.companyId = companyId;
	}

	public String getCategoryCode() {
		return categoryCode;
	}

	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}
}