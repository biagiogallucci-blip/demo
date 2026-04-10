package com.example.demo.projection;

import java.math.BigInteger;

public interface CompaniesByExclusionRuleProjection {
	BigInteger getCompanyId();
    String getCompanyName();
    String getCompanyCode();
    String getIsEnabled();       
    String getDraftIsEnabled();
}