package com.example.demo.response;

import java.util.List;

import com.example.demo.model.ExclusionRulesDto;
import com.example.demo.model.Meta;

public class ExclusionRulesResponse {

	private List<ExclusionRulesDto> data;
	private Meta meta;
	
	public List<ExclusionRulesDto> getData() {
		return data;
	}
	public void setData(List<ExclusionRulesDto> data) {
		this.data = data;
	}
	public Meta getMeta() {
		return meta;
	}
	public void setMeta(Meta meta) {
		this.meta = meta;
	}
}