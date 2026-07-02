package com.example.demo.projection;

import java.math.BigInteger;

public interface ExclusionRulesProjection {
	BigInteger getId();         
    String getCode();
    String getName();
    Long getCompanyCount();
    String getStatus();    
}