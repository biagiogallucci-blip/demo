package com.example.demo.projection;

import java.math.BigInteger;

public interface CompaniesWithCustomParameterProjection {
	BigInteger getCompanyId();
    String getCompanyName();
    String getCompanyCode();
    String getPublishedValue();
    String getPreviewValue();
}