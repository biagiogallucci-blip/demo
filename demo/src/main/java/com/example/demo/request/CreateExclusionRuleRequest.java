package com.example.demo.request;

public class CreateExclusionRuleRequest {

	private String name;
	private String tag;
	private String status;
	private Boolean applyToAllCompanies;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Boolean getApplyToAllCompanies() {
		return applyToAllCompanies;
	}
	public void setApplyToAllCompanies(Boolean applyToAllCompanies) {
		this.applyToAllCompanies = applyToAllCompanies;
	}
}