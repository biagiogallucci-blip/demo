package com.example.demo.projection;

import java.math.BigInteger;

public interface CompanyExclusionRulesProjection {
	BigInteger getId();
    String getTag();
    String getName();
    Integer getIsEnabled();
    Integer getHasDraft();
    Integer getDraftIsEnabled();
    String getStatus();
}