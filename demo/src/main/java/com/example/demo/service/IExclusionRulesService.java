package com.example.demo.service;

import java.math.BigInteger;
import java.util.List;

import com.example.demo.model.ExclusionRulesDto;
import com.example.demo.projection.CompanyLookupProjection;
import com.example.demo.request.CloneExclusionRuleRequest;
import com.example.demo.request.CompanyCreateRequest;
import com.example.demo.request.CreateExclusionRuleRequest;
import com.example.demo.response.CloneExclusionRuleResponse;
import com.example.demo.response.CompaniesByExclusionRuleResponse;
import com.example.demo.response.CreateExclusionRuleResponse;
import com.example.demo.response.ExclusionRulesResponse;

public interface IExclusionRulesService {

	ExclusionRulesResponse getExclusionRules(Integer page, Integer limit, String search, String status);

	CreateExclusionRuleResponse createExclusionRule(CreateExclusionRuleRequest createExclusionRuleRequest);

	ExclusionRulesDto getExclusionRuleById(BigInteger ruleId);

	CompaniesByExclusionRuleResponse getCompaniesByExclusionRuleId(BigInteger ruleId, Integer page, Integer limit, String search);

	List<CompanyLookupProjection> getExclusionRuleLookup(BigInteger ruleId);

	void createExclusionRuleForCompany(BigInteger ruleId, CompanyCreateRequest companyCreateRequest);

	CloneExclusionRuleResponse cloneExclusionRule(BigInteger ruleId, CloneExclusionRuleRequest cloneExclusionRuleRequest);

	void publishExclusionRule(BigInteger ruleId);
}