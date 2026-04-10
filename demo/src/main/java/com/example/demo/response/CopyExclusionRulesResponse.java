package com.example.demo.response;

import java.util.List;

import com.example.demo.model.CompanyExclusionRulesDto;

public class CopyExclusionRulesResponse {
	
	private String message;
	private Integer copiedCount;
	private List<CompanyExclusionRulesDto> data;
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Integer getCopiedCount() {
		return copiedCount;
	}
	public void setCopiedCount(Integer copiedCount) {
		this.copiedCount = copiedCount;
	}
	public List<CompanyExclusionRulesDto> getData() {
		return data;
	}
	public void setData(List<CompanyExclusionRulesDto> data) {
		this.data = data;
	}
}