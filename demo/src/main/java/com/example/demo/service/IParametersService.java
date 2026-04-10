package com.example.demo.service;

import java.math.BigInteger;
import java.util.List;

import com.example.demo.model.ParametersDefinitionDto;
import com.example.demo.projection.CompanyLookupProjection;
import com.example.demo.request.CreateCustomParametersRequest;
import com.example.demo.request.SaveCustomParametersForCompanyRequest;
import com.example.demo.response.CompaniesWithCustomParameterResponse;
import com.example.demo.response.CreateCustomParametersResponse;
import com.example.demo.response.CustomParametersDetailsResponse;
import com.example.demo.response.CustomParametersResponse;

public interface IParametersService {

	List<ParametersDefinitionDto> getParameterDefinitions();

	CustomParametersResponse getCustomParameters(Integer page, Integer limit, String search, String status);

	CreateCustomParametersResponse createCustomParameter(CreateCustomParametersRequest createCustomParametersRequest);

	CustomParametersDetailsResponse getCustomParametersDetails(BigInteger paramId);

	CompaniesWithCustomParameterResponse getCompaniesWithCustomParameter(BigInteger paramId, String search, Integer page, Integer limit);

	void deleteCustomParameters(BigInteger paramId, BigInteger companyId);

	void publishCustomParameters(BigInteger paramId);

	List<CompanyLookupProjection> getCustomParametersLookup(BigInteger paramId);

	void saveCustomParametersForCompany(BigInteger paramId, SaveCustomParametersForCompanyRequest saveCustomParametersForCompanyRequest);

	void putCustomParametersValue(BigInteger paramId, BigInteger companyId, String value);
}