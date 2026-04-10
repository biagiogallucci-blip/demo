package com.example.demo.response;

import java.util.List;

import com.example.demo.model.CompanyParameterDto;

public class CopyCustomParametersResponse {
	
	private String message;
	private Integer copiedCount;
	private List<CompanyParameterDto> data;
	
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
	public List<CompanyParameterDto> getData() {
		return data;
	}
	public void setData(List<CompanyParameterDto> data) {
		this.data = data;
	}
}
