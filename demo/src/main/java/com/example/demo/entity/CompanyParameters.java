package com.example.demo.entity;

import java.io.Serializable;
import java.math.BigInteger;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "COMPANY_PARAMETERS", schema = "XRBNPPUSR", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "COMPANY_ID", "PARAMETER_CODE" }) })
public class CompanyParameters implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "company_parameters_seq_gen")
	@SequenceGenerator(name = "company_parameters_seq_gen",
			sequenceName = "XRBNPPUSR.COMPANY_PARAMETERS_SEQ", 
			allocationSize = 1
	)
	@Column(name = "ID")
	private BigInteger id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COMPANY_ID")
	private Company company;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PARAMETER_CODE")
	private CustomizationParameters parameterCode;
	
	@Column(name = "PARAMETER_VALUE")
	private String parameterValue;

	public BigInteger getId() {
		return id;
	}

	public void setId(BigInteger id) {
		this.id = id;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public CustomizationParameters getParameterCode() {
		return parameterCode;
	}

	public void setParameterCode(CustomizationParameters parameterCode) {
		this.parameterCode = parameterCode;
	}

	public String getParameterValue() {
		return parameterValue;
	}

	public void setParameterValue(String parameterValue) {
		this.parameterValue = parameterValue;
	}
}