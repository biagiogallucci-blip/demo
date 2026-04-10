package com.example.demo.response;

import java.util.List;

import com.example.demo.model.CompaniesByExclusionRuleDto;
import com.example.demo.model.Meta;

public class CompaniesByExclusionRuleResponse {

	private List<CompaniesByExclusionRuleDto> data;
	private Meta meta;

	public List<CompaniesByExclusionRuleDto> getData() {
		return data;
	}

	public void setData(List<CompaniesByExclusionRuleDto> data) {
		this.data = data;
	}

	public Meta getMeta() {
		return meta;
	}

	public void setMeta(Meta meta) {
		this.meta = meta;
	}
}