package com.example.demo.service.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Company;
import com.example.demo.entity.CompanyCategories;
import com.example.demo.model.Actions;
import com.example.demo.model.CompanyDto;
import com.example.demo.model.Configuration;
import com.example.demo.model.ExclusionRulesDto;
import com.example.demo.model.Meta;
import com.example.demo.model.Pagination;
import com.example.demo.repository.CompanyCategoriesRepository;
import com.example.demo.repository.CompanyRepository;
import com.example.demo.response.CompaniesResponse;
import com.example.demo.response.CompanyDetailsResponse;
import com.example.demo.response.CompanyExclusionRulesResponse;
import com.example.demo.service.ICompaniesService;
import com.example.demo.utils.ActiveFlag;

@Service
public class CompaniesService implements ICompaniesService {
    
    @Autowired
    private CompanyRepository companyRepository;
    
    @Autowired
    private CompanyCategoriesRepository companyCategoriesRepository;
    
    @Override
    public CompaniesResponse getCompanies(Integer page, Integer limit, String search) {
        Page<Company> companyPage = companyRepository.searchCompanies(search, PageRequest.of(page-1, limit));

        List<CompanyDto> companyDtos = getCompanyDtos(companyPage);

        Pagination pagination = new Pagination();
        pagination.setCurrentPage(page);
        pagination.setItemsPerPage(limit);
        pagination.setTotalPages(companyPage.getTotalPages());
        pagination.setTotalItems(companyPage.getTotalElements());

        CompaniesResponse response = new CompaniesResponse();
        response.setData(companyDtos);
        Meta meta = new Meta();
        meta.setPagination(pagination);
        response.setMeta(meta);

        return response;
    }

    @Override
    public CompanyDetailsResponse getCompanyDetails(BigInteger companyId) {
        Optional<Company> optionalCompany = companyRepository.findById(companyId);
        CompanyDetailsResponse response = new CompanyDetailsResponse();
        if(optionalCompany.isPresent()) {
            Company company = optionalCompany.get();
            response.setId(company.getIdCompany());
            response.setName(company.getNameCompany());
            response.setCode(company.getCodeCompany());
        } 
        return response;
    }

    @Override
    public CompanyExclusionRulesResponse getCompanyExclusionRules(BigInteger companyId, String search) {
        List<CompanyCategories> result = companyCategoriesRepository.getCompanyExclusionRules(companyId, search);

        List<ExclusionRulesDto> exclusionRules = new ArrayList<>();

        return null;
    }

    private static List<CompanyDto> getCompanyDtos(Page<Company> companyPage) {
        List<CompanyDto> companyDtos = new ArrayList<>();

        for(Company company : companyPage.getContent()) {

            CompanyDto companyDto = new CompanyDto();

            companyDto.setId(company.getIdCompany());
            companyDto.setName(company.getNameCompany());
            companyDto.setCode(company.getCodeCompany());

            Configuration configuration = new Configuration();
            configuration.setHasCustomParameters(ActiveFlag.Y.equals(company.getHasCustomParameters()) ? Boolean.TRUE : Boolean.FALSE);
            configuration.setHasExclusionRules(ActiveFlag.Y.equals(company.getHasExclusionRules()) ? Boolean.TRUE : Boolean.FALSE);

            companyDto.setConfiguration(configuration);
            companyDto.setStatus(company.getStatus());

            Actions actions = new Actions();
            actions.setDetailUrl("/api/v1/companies".concat(String.valueOf(company.getIdCompany())));
            companyDto.setActions(actions);

            companyDtos.add(companyDto);
        }
        return companyDtos;
    }
}