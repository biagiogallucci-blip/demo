package com.example.demo.projection;

public interface CustomParameterListProjection {
	String getCode();
	String getDescription();
	Integer getCompaniesCount();
	Integer getDraftCount();

	default Boolean getHasDraft() {
		return getDraftCount() != null && getDraftCount() > 0;
	}

	default String getStatus() {
		return getHasDraft() ? "DRAFT" : "PUBLISHED";
	}

	default String getPlaceholder() {
		return "$cp_" + getCode() + "_$";
	}

	default String getDetailLink() {
		return "/api/v1/custom-parameters/" + getCode();
	}
}