package com.example.demo.entity;

import java.io.Serializable;
import java.math.BigInteger;

import com.example.demo.utils.ActiveFlag;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "COMPANY", schema = "XRBNPPUSR")
public class Company implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
    @Column(name = "ID_COMPANY", nullable = false)
    private BigInteger idCompany;
	
    @Column(name = "CODE_COMPANY", nullable = false)
    private String codeCompany;

    @Column(name = "NAME_COMPANY")
    private String nameCompany;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "HAS_EXCLUSION_RULES", nullable = false, length = 1)
    private ActiveFlag hasExclusionRules;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "HAS_CUSTOM_PARAMETERS", nullable = false, length = 1)
    private ActiveFlag hasCustomParameters;
    
    @Column(name = "STATUS")
    private String status;

	public BigInteger getIdCompany() {
		return idCompany;
	}

	public void setIdCompany(BigInteger idCompany) {
		this.idCompany = idCompany;
	}

	public String getCodeCompany() {
		return codeCompany;
	}

	public void setCodeCompany(String codeCompany) {
		this.codeCompany = codeCompany;
	}

	public String getNameCompany() {
		return nameCompany;
	}

	public void setNameCompany(String nameCompany) {
		this.nameCompany = nameCompany;
	}

	public ActiveFlag getHasExclusionRules() {
		return hasExclusionRules;
	}

	public void setHasExclusionRules(ActiveFlag hasExclusionRules) {
		this.hasExclusionRules = hasExclusionRules;
	}

	public ActiveFlag getHasCustomParameters() {
		return hasCustomParameters;
	}

	public void setHasCustomParameters(ActiveFlag hasCustomParameters) {
		this.hasCustomParameters = hasCustomParameters;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}