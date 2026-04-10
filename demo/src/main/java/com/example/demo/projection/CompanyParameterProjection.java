package com.example.demo.projection;

import java.math.BigInteger;

public interface CompanyParameterProjection {
	BigInteger getId();

    String getParameterCode();

    String getDescription();

    String getParameterValue();

    String getParameterValuePreview();
}