package com.example.demo.projection;

import java.math.BigInteger;

public interface CompanyExclusionRulesProjection {
	BigInteger getPreviewId();
	String getTag();
	String getName();
	String getIsEnabled();
	String getIsEnabledPreview();
}