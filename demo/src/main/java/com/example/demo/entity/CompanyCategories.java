package com.example.demo.entity;

import java.io.Serializable;

import com.example.demo.utils.ActiveFlag;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "COMPANY_CATEGORIES", schema = "XRBNPPUSR")
public class CompanyCategories implements Serializable {
    private static final long serialVersionUID = 3214253910554454648L;

    @EmbeddedId
    private CompanyCategoryId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("companyId")
    @JoinColumn(name = "COMPANY_ID")
    private Company company;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("categoryCode")
    @JoinColumn(name = "CATEGORY_CODE")
    private Categories category;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "IS_ENABLED", nullable = false, length = 1)
    private ActiveFlag isEnabled;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "HAS_DRAFT", nullable = false, length = 1)
    private ActiveFlag hasDraft;
    
    @Column(name = "STATUS")
    private String status;

	public CompanyCategoryId getId() {
		return id;
	}

	public void setId(CompanyCategoryId id) {
		this.id = id;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Categories getCategory() {
		return category;
	}

	public void setCategory(Categories category) {
		this.category = category;
	}

	public ActiveFlag getIsEnabled() {
		return isEnabled;
	}

	public void setIsEnabled(ActiveFlag isEnabled) {
		this.isEnabled = isEnabled;
	}

	public ActiveFlag getHasDraft() {
		return hasDraft;
	}

	public void setHasDraft(ActiveFlag hasDraft) {
		this.hasDraft = hasDraft;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}