package com.example.demo.response;

import java.math.BigInteger;
import java.sql.Timestamp;

public class CreateExclusionRuleResponse {

	private BigInteger id;
	private String name;
	private String tag;
	private String status;
	private Long companiesCount;
	private Timestamp createdAt;
	
	public BigInteger getId() {
		return id;
	}
	public void setId(BigInteger id) {
		this.id = id;
	}
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
	public Long getCompaniesCount() {
		return companiesCount;
	}
	public void setCompaniesCount(Long companiesCount) {
		this.companiesCount = companiesCount;
	}
	public Timestamp getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}
}