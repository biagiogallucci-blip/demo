package com.example.demo.model;

import java.math.BigInteger;

public class CompaniesByExclusionRuleDto {

	private BigInteger companyId;
	private String companyName;
	private String companyCode;
	private Boolean isEnabled;
	private Boolean hasDraft;
	private DraftExclusionRules draft;
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
	public Boolean getIsEnabled() {
		return isEnabled;
	}
	public void setIsEnabled(Boolean isEnabled) {
		this.isEnabled = isEnabled;
	}
	public Boolean getHasDraft() {
		return hasDraft;
	}
	public void setHasDraft(Boolean hasDraft) {
		this.hasDraft = hasDraft;
	}
	public DraftExclusionRules getDraft() {
		return draft;
	}
	public void setDraft(DraftExclusionRules draft) {
		this.draft = draft;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}