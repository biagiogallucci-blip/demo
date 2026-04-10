package com.example.demo.model;

public class CustomParametersDto {
	private String id;
    private String title;
    private String code;
    private String placeholder;
    private Integer companiesCount;
    private Boolean hasDraft;
    private String status;
    private String detail;
	public String getId() {
		return id;
	}
	public void setId(String id) {
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
	public Integer getCompaniesCount() {
		return companiesCount;
	}
	public void setCompaniesCount(Integer companiesCount) {
		this.companiesCount = companiesCount;
	}
	public Boolean getHasDraft() {
		return hasDraft;
	}
	public void setHasDraft(Boolean hasDraft) {
		this.hasDraft = hasDraft;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}
}