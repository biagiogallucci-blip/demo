package com.example.demo.projection;

public interface CompanyLookupProjection {
	String getId();
    String getName();
    String getCode();
    Long getExclusionRulesCount();
    Long getParametersCount();
}