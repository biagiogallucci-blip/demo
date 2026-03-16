package com.example.demo.service;

import java.math.BigInteger;

import com.example.demo.response.CompaniesResponse;
import com.example.demo.response.CompanyDetailsResponse;
import com.example.demo.response.CompanyExclusionRulesResponse;

public interface ICompaniesService {
    CompaniesResponse getCompanies(Integer page, Integer limit, String search);

    CompanyDetailsResponse getCompanyDetails(BigInteger companyId);

    CompanyExclusionRulesResponse getCompanyExclusionRules(BigInteger companyId, String search);
}