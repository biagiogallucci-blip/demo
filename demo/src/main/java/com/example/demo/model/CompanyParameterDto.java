package com.example.demo.model;

import java.math.BigInteger;

public class CompanyParameterDto {
	private BigInteger id;
	private String title;
	private String code;
	private String variableName;
	private String value;
	private Boolean hasDraft;
	private DraftCustomParameters draft;
	private String status;
	
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
	public String getVariableName() {
		return variableName;
	}
	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public Boolean getHasDraft() {
		return hasDraft;
	}
	public void setHasDraft(Boolean hasDraft) {
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
