package com.example.demo.response;

import java.util.List;

import com.example.demo.model.ExclusionRulesDto;

public class CompanyExclusionRulesResponse {
	
	private List<ExclusionRulesDto> data;

	public List<ExclusionRulesDto> getData() {
		return data;
	}

	public void setData(List<ExclusionRulesDto> data) {
		this.data = data;
	}
}