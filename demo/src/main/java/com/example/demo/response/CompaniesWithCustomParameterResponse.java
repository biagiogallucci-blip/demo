package com.example.demo.response;

import java.util.List;

import com.example.demo.model.CompaniesWithCustomParameterDto;
import com.example.demo.model.Meta;

public class CompaniesWithCustomParameterResponse {
	
	private List<CompaniesWithCustomParameterDto> data;
	private Meta meta;
	
	public List<CompaniesWithCustomParameterDto> getData() {
		return data;
	}
	public void setData(List<CompaniesWithCustomParameterDto> data) {
		this.data = data;
	}
	public Meta getMeta() {
		return meta;
	}
	public void setMeta(Meta meta) {
		this.meta = meta;
	}
}