package com.example.demo.entity;

import java.io.Serializable;
import java.math.BigInteger;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "CUSTOMIZATION_PARAMETERS", schema = "XRBNPPUSR")
public class CustomizationParameters implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "CODE", length = 100)
	private String code;
	
	@Column(name = "DESCRIPTION", length = 200)
	private String description;
	
	@Column(name = "ID", updatable = false, nullable = false)
	private BigInteger id;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigInteger getId() {
		return id;
	}

	public void setId(BigInteger id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "CustomizationParameters [code=" + code + ", description=" + description + ", id=" + id + "]";
	}
}