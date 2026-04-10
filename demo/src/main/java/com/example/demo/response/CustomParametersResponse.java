package com.example.demo.response;

import java.util.List;

import com.example.demo.model.CustomParametersDto;
import com.example.demo.model.Meta;

public class CustomParametersResponse {
	
	private List<CustomParametersDto> data;
    private Meta meta;
	public List<CustomParametersDto> getData() {
		return data;
	}
	public void setData(List<CustomParametersDto> data) {
		this.data = data;
	}
	public Meta getMeta() {
		return meta;
	}
	public void setMeta(Meta meta) {
		this.meta = meta;
	}
}