package com.example.demo.response;

import java.util.List;

import com.example.demo.model.CompanyExclusionRulesDto;

public class CompanyExclusionRulesResponse {
	
	private List<CompanyExclusionRulesDto> data;

	public List<CompanyExclusionRulesDto> getData() {
		return data;
	}

	public void setData(List<CompanyExclusionRulesDto> data) {
		this.data = data;
	}
}