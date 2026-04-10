package com.example.demo.entity;

import java.io.Serializable;
import java.math.BigInteger;

import com.example.demo.utils.ActiveFlag;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "COMPANY_CATEGORIES_PREVIEW", schema = "XRBNPPUSR")
public class CompanyCategoriesPreview implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "company_categories_preview_seq_gen")
	@SequenceGenerator(name = "company_categories_preview_seq_gen",
			sequenceName = "XRBNPPUSR.COMPANY_CATEGORIES_PREVIEW_SEQ", 
			allocationSize = 1
	)
	@Column(name = "ID")
	private BigInteger id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COMPANY_ID")
	private Company company;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CATEGORY_CODE")
	private Categories category;
	
	@Enumerated(EnumType.STRING)
    @Column(name = "IS_ENABLED", nullable = false, length = 1)
    private ActiveFlag isEnabled;

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
}