package com.example.demo.model;

import java.math.BigInteger;
import java.sql.Timestamp;

import com.example.demo.response.Stats;

public class ExclusionRulesDto {
	
	private BigInteger id;
	private String name;
	private String tag;
	private Integer companiesCount;
	private Boolean hasDraft;
	private String status;
	private String links;
	private Timestamp createdAt;
	private Stats stats;
	
	public BigInteger getId() {
		return id;
	}
	public void setId(BigInteger id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public Integer getCompaniesCount() {
		return companiesCount;
	}
	public void setCompaniesCount(Integer companiesCount) {
		this.companiesCount = companiesCount;
	}
	public Boolean getHasDraft() {
		return hasDraft;
	}
	public void setHasDraft(Boolean hasDraft) {
		this.hasDraft = hasDraft;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getLinks() {
		return links;
	}
	public void setLinks(String links) {
		this.links = links;
	}
	public Timestamp getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}
	public Stats getStats() {
		return stats;
	}
	public void setStats(Stats stats) {
		this.stats = stats;
	}
}