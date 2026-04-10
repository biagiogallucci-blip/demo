package com.example.demo.request;

public class CreateCustomParametersRequest {

	private String title;
	private String code;
	private String value;
	private Boolean applyToAllCompanies;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public Boolean getApplyToAllCompanies() {
		return applyToAllCompanies;
	}
	public void setApplyToAllCompanies(Boolean applyToAllCompanies) {
		this.applyToAllCompanies = applyToAllCompanies;
	}
}