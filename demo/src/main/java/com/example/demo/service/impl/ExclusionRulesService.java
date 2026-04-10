package com.example.demo.service.impl;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.Categories;
import com.example.demo.entity.Company;
import com.example.demo.entity.CompanyCategories;
import com.example.demo.entity.CompanyCategoriesPreview;
import com.example.demo.handler.CategoriesAlreadyExistException;
import com.example.demo.handler.CategoriesNotFoundException;
import com.example.demo.handler.CompanyNotFoundException;
import com.example.demo.model.CompaniesByExclusionRuleDto;
import com.example.demo.model.DraftExclusionRules;
import com.example.demo.model.ExclusionRulesDto;
import com.example.demo.model.Meta;
import com.example.demo.model.Pagination;
import com.example.demo.projection.CategoriesWithDraftProjection;
import com.example.demo.projection.CloneExclusionRuleProjection;
import com.example.demo.projection.CompaniesByExclusionRuleProjection;
import com.example.demo.projection.CompanyLookupProjection;
import com.example.demo.projection.ExclusionRulesProjection;
import com.example.demo.projection.PublishExclusionRuleProjection;
import com.example.demo.repository.CategoriesRepository;
import com.example.demo.repository.CompanyCategoriesPreviewRepository;
import com.example.demo.repository.CompanyCategoriesRepository;
import com.example.demo.repository.CompanyRepository;
import com.example.demo.request.CloneExclusionRuleRequest;
import com.example.demo.request.CompanyCreateRequest;
import com.example.demo.request.CreateExclusionRuleRequest;
import com.example.demo.response.CloneExclusionRuleResponse;
import com.example.demo.response.CompaniesByExclusionRuleResponse;
import com.example.demo.response.CreateExclusionRuleResponse;
import com.example.demo.response.ExclusionRulesResponse;
import com.example.demo.service.ICompaniesService;
import com.example.demo.service.IExclusionRulesService;
import com.example.demo.utils.ActiveFlag;
import com.example.demo.utils.Constants;

@Service
public class ExclusionRulesService implements IExclusionRulesService {
	
	@Autowired
	private CategoriesRepository categoriesRepository;

	@Autowired
	private CompanyRepository companyRepository;

	@Autowired
	private CompanyCategoriesRepository companyCategoriesRepository;

	@Autowired
	private CompanyCategoriesPreviewRepository companyCategoriesPreviewRepository;
	
	@Autowired
	private ICompaniesService companiesService;

	@Override
	public ExclusionRulesResponse getExclusionRules(Integer page, Integer limit, String search, String status) {
		Page<ExclusionRulesProjection> exclusionRulePage = categoriesRepository.getExclusionRules(search, status, PageRequest.of(page-1, limit));
		
		List<ExclusionRulesDto> exclusionRules = new ArrayList<>();
		
		for(ExclusionRulesProjection projection : exclusionRulePage.getContent()) {
			ExclusionRulesDto exclusionRule = new ExclusionRulesDto();
			exclusionRule.setId(projection.getId());
			exclusionRule.setName(projection.getName());
			exclusionRule.setTag(projection.getCode());
			exclusionRule.setCompaniesCount(projection.getCompanyCount());
			exclusionRule.setLinks("/api/v1/exclusion-rules/".concat(String.valueOf(projection.getId())));
			exclusionRule.setStatus(projection.getStatus());
			if(Constants.DRAFT.equals(projection.getStatus())) {
				exclusionRule.setHasDraft(Boolean.TRUE);
			} else {
				exclusionRule.setHasDraft(Boolean.FALSE);
			}
			exclusionRules.add(exclusionRule);
		}
		
		Pagination pagination = new Pagination();
        pagination.setCurrentPage(page);
        pagination.setItemsPerPage(limit);
        pagination.setTotalPages(exclusionRulePage.getTotalPages());
        pagination.setTotalItems(exclusionRulePage.getTotalElements());
        
        Meta meta = new Meta();
        meta.setPagination(pagination);
        
        ExclusionRulesResponse response = new ExclusionRulesResponse();
		response.setData(exclusionRules);
		response.setMeta(meta);
		
		return response;
	}

	@Override
	@Transactional
	public CreateExclusionRuleResponse createExclusionRule(CreateExclusionRuleRequest createExclusionRuleRequest) {
		categoriesRepository.findById(createExclusionRuleRequest.getTag()).ifPresent(category -> {
			throw new CategoriesAlreadyExistException(createExclusionRuleRequest.getTag());
		});

		Categories category = new Categories();
		category.setCode(createExclusionRuleRequest.getTag());
		category.setName(createExclusionRuleRequest.getName());
		category.setActive(ActiveFlag.Y);
		category.setCreation(new Timestamp(System.currentTimeMillis()));
		category.setId(categoriesRepository.getNextId());

		categoriesRepository.saveAndFlush(category);

		List<Company> companies = new ArrayList<>();

		if (Boolean.TRUE.equals(createExclusionRuleRequest.getApplyToAllCompanies())) {
			companies = companyRepository.findAll();
			for (Company company : companies) {
				CompanyCategories entity = new CompanyCategories();
				entity.setCompany(company);
				entity.setCategory(category);
				entity.setIsEnabled(ActiveFlag.Y);
				companyCategoriesRepository.save(entity);

				CompanyCategoriesPreview entityPreview = new CompanyCategoriesPreview();
				entityPreview.setCompany(company);
				entityPreview.setCategory(category);
				entityPreview.setIsEnabled(ActiveFlag.Y);
				companyCategoriesPreviewRepository.save(entityPreview);
			}
		}

		CreateExclusionRuleResponse response = new CreateExclusionRuleResponse();
		response.setId(category.getId());
		response.setName(category.getName());
		response.setTag(category.getCode());
		response.setStatus(createExclusionRuleRequest.getStatus());
		response.setCompaniesCount(companies.size());
		response.setCreatedAt(category.getCreation());

		return response;
	}

	@Override
	public ExclusionRulesDto getExclusionRuleById(BigInteger ruleId) {
		CategoriesWithDraftProjection projection = categoriesRepository.getByRuleId(ruleId)
				.orElseThrow(() -> new CategoriesNotFoundException(ruleId));
		
		ExclusionRulesDto response = new ExclusionRulesDto();
		
		Categories category = projection.getCategory();
		
		response.setId(category.getId());
		response.setName(category.getName());
		response.setTag(category.getCode());
		response.setCreatedAt(category.getCreation());
		response.setStatus(projection.getStatus());
		if(Constants.DRAFT.equals(projection.getStatus())) {
			response.setHasDraft(Boolean.TRUE);
		} else {
			response.setHasDraft(Boolean.FALSE);
		}
		
	    return response;
	}

	@Override
	public CompaniesByExclusionRuleResponse getCompaniesByExclusionRuleId(BigInteger ruleId, Integer page, Integer limit, String search) {
		Page<CompaniesByExclusionRuleProjection> result = companyCategoriesRepository.getCompaniesByExclusionRuleId(ruleId, search, PageRequest.of(page-1, limit));
		
		List<CompaniesByExclusionRuleDto> companiesByExclusionRule = new ArrayList<>();
		
		for(CompaniesByExclusionRuleProjection projection : result.getContent()) {
			CompaniesByExclusionRuleDto dto = new CompaniesByExclusionRuleDto();
			
			boolean hasDraft = projection.getDraftIsEnabled() != null &&
	                !projection.getDraftIsEnabled().equals(projection.getIsEnabled());
			
			String status;
	        if (hasDraft) {
	            status = Constants.PENDING_PUBLICATION;
	        } else {
	            status = Constants.PUBLISHED;
	        }
			
			dto.setCompanyId(projection.getCompanyId());
			dto.setCompanyName(projection.getCompanyName());
			dto.setCompanyCode(projection.getCompanyCode());
			dto.setIsEnabled(Constants.YES.equals(projection.getIsEnabled()) ? Boolean.TRUE : Boolean.FALSE);
			dto.setHasDraft(hasDraft);
			dto.setStatus(status);
			
			if(hasDraft) {
				DraftExclusionRules draft = new DraftExclusionRules();
				draft.setIsEnabled(Constants.YES.equals(projection.getDraftIsEnabled()) ? Boolean.TRUE : Boolean.FALSE);
				draft.setStatus(Constants.DRAFT);
				dto.setDraft(draft);
			}
			companiesByExclusionRule.add(dto);
		}
		
		Pagination pagination = new Pagination();
        pagination.setCurrentPage(page);
        pagination.setItemsPerPage(limit);
        pagination.setTotalPages(result.getTotalPages());
        pagination.setTotalItems(result.getTotalElements());
        
        Meta meta = new Meta();
        meta.setPagination(pagination);
        
        CompaniesByExclusionRuleResponse response = new CompaniesByExclusionRuleResponse();
        response.setData(companiesByExclusionRule);
        response.setMeta(meta);
		
		return response;
	}

	@Override
	public List<CompanyLookupProjection> getExclusionRuleLookup(BigInteger ruleId) {
		return companyRepository.getExclusionRuleLookup(ruleId);
	}

	@Override
	@Transactional
	public void createExclusionRuleForCompany(BigInteger ruleId, CompanyCreateRequest companyCreateRequest) {
		CategoriesWithDraftProjection projection = categoriesRepository.getByRuleId(ruleId)
				.orElseThrow(() -> new CategoriesNotFoundException(ruleId));
		
		Company company = companyRepository.findById(companyCreateRequest.getSourceCompanyId())
				.orElseThrow(() -> new CompanyNotFoundException(companyCreateRequest.getSourceCompanyId()));
		
		CompanyCategories entity = new CompanyCategories();
		entity.setCompany(company);
		entity.setCategory(projection.getCategory());
		entity.setIsEnabled(ActiveFlag.Y);
		companyCategoriesRepository.save(entity);

		CompanyCategoriesPreview entityPreview = new CompanyCategoriesPreview();
		entityPreview.setCompany(company);
		entityPreview.setCategory(projection.getCategory());
		entityPreview.setIsEnabled(ActiveFlag.Y);
		companyCategoriesPreviewRepository.save(entityPreview);
	}

	@Override
	@Transactional
	public CloneExclusionRuleResponse cloneExclusionRule(BigInteger ruleId, CloneExclusionRuleRequest cloneExclusionRuleRequest) {
		Categories categories = new Categories();
		categories.setName(cloneExclusionRuleRequest.getNewName());
		categories.setCode(cloneExclusionRuleRequest.getNewTag());
		categories.setActive(ActiveFlag.Y);
		categories.setCreation(Timestamp.from(Instant.now()));
		categories.setId(categoriesRepository.getNextId());
		
		categoriesRepository.save(categories);
		
		List<CloneExclusionRuleProjection> result = companyCategoriesRepository.findForCloneExclusionRule(ruleId);
		
		for(CloneExclusionRuleProjection projection : result) {
			CompanyCategories entity = new CompanyCategories();
			entity.setCompany(projection.getCompany());
			entity.setCategory(categories);
			entity.setIsEnabled(projection.getIsEnabled());
			companyCategoriesRepository.save(entity);

			CompanyCategoriesPreview entityPreview = new CompanyCategoriesPreview();
			entityPreview.setCompany(projection.getCompany());
			entityPreview.setCategory(categories);
			entityPreview.setIsEnabled(projection.getIsEnabled());
			companyCategoriesPreviewRepository.save(entityPreview);
		}
		
		CloneExclusionRuleResponse response = new CloneExclusionRuleResponse();
		response.setId(categories.getId());
		response.setName(categories.getName());
		
		return response;
	}

	@Override
	public void publishExclusionRule(BigInteger ruleId) {
		List<PublishExclusionRuleProjection> result = companyCategoriesPreviewRepository.findExclusionRuleToPublish(ruleId);
		
		for(PublishExclusionRuleProjection projection : result) {
			companiesService.publishExclusionRules(projection.getCompanyId(), projection.getCompanyCategoriesPreviewId());
		}
	}
}