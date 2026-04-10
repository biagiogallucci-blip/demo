package com.example.demo.response;

import java.math.BigInteger;

public class CreateCustomParametersResponse {

	private BigInteger id;
	private String title;
	private String code;
	private String placeholder;
	private String value;
	private String status;
	private Integer companiesCount;
	
	public BigInteger getId() {
		return id;
	}
	public void setId(BigInteger id) {
		this.id = id;
	}
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
	public String getPlaceholder() {
		return placeholder;
	}
	public void setPlaceholder(String placeholder) {
		this.placeholder = placeholder;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Integer getCompaniesCount() {
		return companiesCount;
	}
	public void setCompaniesCount(Integer companiesCount) {
		this.companiesCount = companiesCount;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}