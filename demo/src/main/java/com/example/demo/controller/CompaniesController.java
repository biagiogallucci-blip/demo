package com.example.demo.controller;

import java.math.BigInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.response.CompaniesResponse;
import com.example.demo.response.CompanyDetailsResponse;
import com.example.demo.response.CompanyExclusionRulesResponse;
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
}