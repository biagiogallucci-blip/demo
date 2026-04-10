package com.example.demo.response;

import java.util.List;

import com.example.demo.model.CompanyParameterDto;

public class CompanyCustomParametersResponse {
	
	private List<CompanyParameterDto> data;

	public List<CompanyParameterDto> getData() {
		return data;
	}

	public void setData(List<CompanyParameterDto> data) {
		this.data = data;
	}
}