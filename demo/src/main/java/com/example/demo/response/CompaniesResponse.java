package com.example.demo.response;

import java.util.List;

import com.example.demo.model.CompanyDto;
import com.example.demo.model.Meta;

public class CompaniesResponse {
	
	private List<CompanyDto> data;
	private Meta meta;
	
	public List<CompanyDto> getData() {
		return data;
	}
	public void setData(List<CompanyDto> data) {
		this.data = data;
	}
	public Meta getMeta() {
		return meta;
	}
	public void setMeta(Meta meta) {
		this.meta = meta;
	}
}