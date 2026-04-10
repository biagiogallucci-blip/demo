package com.example.demo.projection;

import java.math.BigInteger;

public interface CompanyProjection {
    BigInteger getIdCompany();
    String getCodeCompany();
    String getNameCompany();
    String getStatus();
    Boolean getHasCategory();
    Boolean getHasParameters();
}