package com.example.demo.authprofiles;

import java.util.List;

import com.example.demo.model.Meta;

public class UsersResponse {

	private List<UserDto> data;
    private Meta meta;
    
	public List<UserDto> getData() {
		return data;
	}
	public void setData(List<UserDto> data) {
		this.data = data;
	}
	public Meta getMeta() {
		return meta;
	}
	public void setMeta(Meta meta) {
		this.meta = meta;
	}
	@Override
	public String toString() {
		return "UsersResponse [data=" + data + ", meta=" + meta + "]";
	}
}