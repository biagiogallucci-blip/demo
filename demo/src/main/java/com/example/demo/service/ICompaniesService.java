package com.example.demo.service;

import java.math.BigInteger;
import java.util.List;

import com.example.demo.model.CompanyParameterDto;
import com.example.demo.projection.CompanyLookupProjection;
import com.example.demo.request.AddCustomParametersRequest;
import com.example.demo.request.CompanyCreateRequest;
import com.example.demo.request.CreateExclusionDraftRequest;
import com.example.demo.request.CreateParameterDraftRequest;
import com.example.demo.response.CompaniesResponse;
import com.example.demo.response.CompanyCustomParametersResponse;
import com.example.demo.response.CompanyDetailsResponse;
import com.example.demo.response.CompanyExclusionRulesResponse;
import com.example.demo.response.CopyCustomParametersResponse;
import com.example.demo.response.CopyExclusionRulesResponse;

public interface ICompaniesService {
    CompaniesResponse getCompanies(Integer page, Integer limit, String search);

    CompanyDetailsResponse getCompanyDetails(BigInteger companyId);

    CompanyExclusionRulesResponse getCompanyExclusionRules(BigInteger companyId, String search);

    List<CompanyLookupProjection> getCompanyLookup(BigInteger currentCompanyId);

    CopyExclusionRulesResponse copyExclusionRules(BigInteger companyId, CompanyCreateRequest copyExclusionRulesRequest);

	void updateDraft(BigInteger companyId, BigInteger ruleId, CreateExclusionDraftRequest createDraftRequest);

	CompanyCustomParametersResponse getCompanyCustomParameters(BigInteger companyId, String search);

	void createCompanyParameterDraft(BigInteger companyId, BigInteger paramId,
			CreateParameterDraftRequest createDraftRequest);

	void publishCompanyParameterDraft(BigInteger companyId, BigInteger paramId);

	CopyCustomParametersResponse copyCustomParameters(BigInteger companyId,
			CompanyCreateRequest copyExclusionRulesRequest);

	void publishExclusionRules(BigInteger companyId, BigInteger ruleId);

	CompanyParameterDto addCustomParameters(BigInteger companyId,
			AddCustomParametersRequest addCustomParametersRequest);
}