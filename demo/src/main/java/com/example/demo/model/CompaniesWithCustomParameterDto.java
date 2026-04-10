package com.example.demo.model;

import java.math.BigInteger;

public class CompaniesWithCustomParameterDto {
	private BigInteger companyId;
    private String companyName;
    private String companyCode;
    private String value; 
    private boolean hasDraft;
    private DraftCustomParameters draft;
    private String status;
    
	public BigInteger getCompanyId() {
		return companyId;
	}
	public void setCompanyId(BigInteger companyId) {
		this.companyId = companyId;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getCompanyCode() {
		return companyCode;
	}
	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public boolean isHasDraft() {
		return hasDraft;
	}
	public void setHasDraft(boolean hasDraft) {
		this.hasDraft = hasDraft;
	}
	public DraftCustomParameters getDraft() {
		return draft;
	}
	public void setDraft(DraftCustomParameters draft) {
		this.draft = draft;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}