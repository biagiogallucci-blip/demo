package com.example.demo.model;

import java.math.BigInteger;

public class ExclusionRulesDto {
	
	private BigInteger id;
	private String name;
	private String tag;
	private Boolean isEnabled;
	private Boolean hasDraft;
	private String status;
	
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}