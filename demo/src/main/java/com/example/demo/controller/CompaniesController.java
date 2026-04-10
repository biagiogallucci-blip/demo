package com.example.demo.controller;

import java.math.BigInteger;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
import com.example.demo.service.ICompaniesService;

@RestController
@RequestMapping("/api/v1/companies")
public class CompaniesController {

    @Autowired
    private ICompaniesService companiesService;

    @GetMapping
    public CompaniesResponse getCompanies(@RequestParam(defaultValue = "1") Integer page,
                                          @RequestParam(defaultValue = "10") Integer limit,
                                          @RequestParam(required = false) String search) {
        return companiesService.getCompanies(page, limit, search);
    }

    @GetMapping("/{companyId}")
    public CompanyDetailsResponse getCompanyDetails(@PathVariable("companyId") BigInteger companyId) {
        return companiesService.getCompanyDetails(companyId);
    }

    @GetMapping("/{companyId}/exclusion-rules")
    public CompanyExclusionRulesResponse getCompanyExclusionRules(@PathVariable("companyId") BigInteger companyId,
                                                                  @RequestParam(required = false) String search) {
        return companiesService.getCompanyExclusionRules(companyId, search);
    }
    
    @GetMapping("/lookup/{currentCompanyId}")
    public List<CompanyLookupProjection> getCompanyLookup(@PathVariable("currentCompanyId") BigInteger currentCompanyId) {
        return companiesService.getCompanyLookup(currentCompanyId);
    }
    
    @PostMapping("/{companyId}/exclusion-rules/copy")
    public CopyExclusionRulesResponse copyExclusionRules(@PathVariable("companyId") BigInteger companyId, 
    		@RequestBody CompanyCreateRequest copyExclusionRulesRequest) {
        return companiesService.copyExclusionRules(companyId, copyExclusionRulesRequest);
    }
    
    @PatchMapping("/{companyId}/exclusion-rules/{ruleId}")
    public void updateDraft (@PathVariable("companyId") BigInteger companyId, @PathVariable("ruleId") BigInteger ruleId,
    		@RequestBody CreateExclusionDraftRequest createDraftRequest) {
    	companiesService.updateDraft(companyId, ruleId, createDraftRequest);
    }
    
    @PostMapping("/{companyId}/exclusion-rules/{ruleId}/publish")
    public void publishExclusionRules (@PathVariable("companyId") BigInteger companyId,
    		@PathVariable("ruleId") BigInteger ruleId) {
        companiesService.publishExclusionRules(companyId, ruleId);
    }
    
    @GetMapping("/{companyId}/custom-parameters")
    public CompanyCustomParametersResponse getCompanyCustomParameters(@PathVariable("companyId") BigInteger companyId,
                                                                  @RequestParam(required = false) String search) {
        return companiesService.getCompanyCustomParameters(companyId, search);
    }
    
    @PutMapping("/{companyId}/custom-parameters/{paramId}")
    public void createCompanyParameterDraft (@PathVariable("companyId") BigInteger companyId,
    		@PathVariable("paramId") BigInteger paramId, @RequestBody CreateParameterDraftRequest createDraftRequest) {
        companiesService.createCompanyParameterDraft(companyId, paramId, createDraftRequest);
    }
    
    @PostMapping("/{companyId}/custom-parameters/{paramId}/publish")
    public void publishCompanyParameterDraft (@PathVariable("companyId") BigInteger companyId,
    		@PathVariable("paramId") BigInteger paramId) {
        companiesService.publishCompanyParameterDraft(companyId, paramId);
    }
    
    @PostMapping("/{companyId}/custom-parameters/copy")
    public CopyCustomParametersResponse copyCustomParameters(@PathVariable("companyId") BigInteger companyId, 
    		@RequestBody CompanyCreateRequest copyExclusionRulesRequest) throws Exception {
        return companiesService.copyCustomParameters(companyId, copyExclusionRulesRequest);
    }
    
    @PostMapping("/{companyId}/custom-parameters")
    public CompanyParameterDto addCustomParameters (@PathVariable("companyId") BigInteger companyId,
    		@RequestBody AddCustomParametersRequest addCustomParametersRequest) {
        return companiesService.addCustomParameters(companyId, addCustomParametersRequest);
    }
}