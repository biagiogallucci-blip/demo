package com.example.demo.service.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.Company;
import com.example.demo.entity.CompanyCategories;
import com.example.demo.entity.CompanyCategoriesPreview;
import com.example.demo.entity.CompanyParameters;
import com.example.demo.entity.CompanyParametersPreview;
import com.example.demo.entity.CustomizationParameters;
import com.example.demo.handler.CompanyCategoriesNotFoundException;
import com.example.demo.handler.CompanyNotFoundException;
import com.example.demo.handler.CompanyParametersNotFoundException;
import com.example.demo.handler.CompanyParametersPreviewNotFoundException;
import com.example.demo.handler.CustomizationParametersNotFoundException;
import com.example.demo.model.Actions;
import com.example.demo.model.CompanyDto;
import com.example.demo.model.CompanyParameterDto;
import com.example.demo.model.Configuration;
import com.example.demo.model.DraftCustomParameters;
import com.example.demo.model.DraftExclusionRules;
import com.example.demo.model.CompanyExclusionRulesDto;
import com.example.demo.model.Meta;
import com.example.demo.model.Pagination;
import com.example.demo.projection.CompanyExclusionRulesProjection;
import com.example.demo.projection.CompanyLookupProjection;
import com.example.demo.projection.CompanyParameterProjection;
import com.example.demo.projection.CompanyProjection;
import com.example.demo.repository.CompanyCategoriesPreviewRepository;
import com.example.demo.repository.CompanyCategoriesRepository;
import com.example.demo.repository.CompanyParametersPreviewRepository;
import com.example.demo.repository.CompanyParametersRepository;
import com.example.demo.repository.CompanyRepository;
import com.example.demo.repository.CustomizationParametersRepository;
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
import com.example.demo.utils.ActiveFlag;
import com.example.demo.utils.Constants;

@Service
public class CompaniesService implements ICompaniesService {
    
    @Autowired
    private CompanyRepository companyRepository;
    
    @Autowired
    private CompanyCategoriesRepository companyCategoriesRepository;
    
    @Autowired
    private CompanyCategoriesPreviewRepository companyCategoriesPreviewRepository;
    
    @Autowired
    private CompanyParametersRepository companyParametersRepository;
    
    @Autowired
    private CompanyParametersPreviewRepository companyParametersPreviewRepository;
    
    @Autowired
    private CustomizationParametersRepository customizationParametersRepository;
    
    @Override
    public CompaniesResponse getCompanies(Integer page, Integer limit, String search) {
        Page<CompanyProjection> companyPage = companyRepository.searchCompanies(search, PageRequest.of(page-1, limit));

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
		Company company = companyRepository.findById(companyId)
				.orElseThrow(() -> new CompanyNotFoundException(companyId));
		
		CompanyDetailsResponse response = new CompanyDetailsResponse();
		
		response.setId(company.getIdCompany());
		response.setName(company.getNameCompany());
		response.setCode(company.getCodeCompany());
		
		return response;
	}

    @Override
    public CompanyExclusionRulesResponse getCompanyExclusionRules(BigInteger companyId, String search) {
        List<CompanyExclusionRulesProjection> result = companyCategoriesRepository.getCompanyExclusionRules(companyId, search);
        
        List<CompanyExclusionRulesDto> exclusionRules = new ArrayList<>();
        
        for(CompanyExclusionRulesProjection projection : result) {
        	CompanyExclusionRulesDto dto = new CompanyExclusionRulesDto();
        	dto.setId(projection.getPreviewId());
        	dto.setName(projection.getName());
        	dto.setTag(projection.getTag());
        	dto.setIsEnabled(Constants.YES.equals(projection.getIsEnabled()) ? Boolean.TRUE : Boolean.FALSE);
        	if(projection.getIsEnabled().equals(projection.getIsEnabledPreview())) {
        		dto.setHasDraft(Boolean.FALSE);
        		dto.setStatus(Constants.PUBLISHED);
        	} else {
        		dto.setHasDraft(Boolean.TRUE);
        		dto.setStatus(Constants.PENDING_PUBLICATION);
        		DraftExclusionRules draft = new DraftExclusionRules();
        		draft.setStatus(Constants.DRAFT);
        		draft.setIsEnabled(Constants.YES.equals(projection.getIsEnabledPreview()) ? Boolean.TRUE : Boolean.FALSE);
				dto.setDraft(draft);
        	}
        	exclusionRules.add(dto);
        }
        
        CompanyExclusionRulesResponse response = new CompanyExclusionRulesResponse();
        response.setData(exclusionRules);

        return response;
    }

    private static List<CompanyDto> getCompanyDtos(Page<CompanyProjection> companyPage) {
        List<CompanyDto> companyDtos = new ArrayList<>();

        for(CompanyProjection company : companyPage.getContent()) {

            CompanyDto companyDto = new CompanyDto();

            companyDto.setId(company.getIdCompany());
            companyDto.setName(company.getNameCompany());
            companyDto.setCode(company.getCodeCompany());

            Configuration configuration = new Configuration();
           configuration.setHasCustomParameters(company.getHasParameters());
           configuration.setHasExclusionRules(company.getHasCategory());

            companyDto.setConfiguration(configuration);
            if(Boolean.TRUE.equals(company.getHasCategory())) {
            	companyDto.setStatus(Constants.DRAFT);
            } else {
            	companyDto.setStatus(Constants.PUBLISHED);
            }

            Actions actions = new Actions();
            actions.setDetailUrl("/api/v1/companies/".concat(String.valueOf(company.getIdCompany())));
            companyDto.setActions(actions);

            companyDtos.add(companyDto);
        }
        return companyDtos;
    }

	@Override
	public List<CompanyLookupProjection> getCompanyLookup(BigInteger currentCompanyId) {
		return companyRepository.getCompanyLookup(currentCompanyId);
	}

	@Override
	@Transactional
	public CopyExclusionRulesResponse copyExclusionRules(BigInteger companyId, CompanyCreateRequest copyExclusionRulesRequest) {
		Company company = companyRepository.findById(companyId)
				.orElseThrow(() -> new CompanyNotFoundException(companyId));
		
		companyCategoriesRepository.deleteByCompanyId(companyId);
		companyCategoriesPreviewRepository.deleteByCompanyId(companyId);
		
		List<CompanyCategories> categoriesToCopy = companyCategoriesRepository.findCategoriesByCompanyId(copyExclusionRulesRequest.getSourceCompanyId());
		
		for(CompanyCategories category : categoriesToCopy) {
			CompanyCategories entity = new CompanyCategories();
			entity.setCompany(company);
			entity.setCategory(category.getCategory());
			entity.setIsEnabled(category.getIsEnabled());
			companyCategoriesRepository.save(entity);
			
			CompanyCategoriesPreview entityPreview = new CompanyCategoriesPreview();
			entityPreview.setCompany(company);
			entityPreview.setCategory(category.getCategory());
			entityPreview.setIsEnabled(category.getIsEnabled());
			companyCategoriesPreviewRepository.save(entityPreview);
		}
		
		List<CompanyCategories> copiedExclusions = companyCategoriesRepository.findCategoriesByCompanyId(companyId);
		List<CompanyExclusionRulesDto> rules = new ArrayList<>();
		
		for(CompanyCategories category : copiedExclusions) {
			CompanyExclusionRulesDto rule = new CompanyExclusionRulesDto();
			rule.setId(category.getId());
			rule.setName(category.getCategory().getName());
			rule.setTag(category.getCategory().getCode());
			rule.setIsEnabled(Constants.YES.equals(category.getIsEnabled().name()) ? Boolean.TRUE : Boolean.FALSE);
			rule.setHasDraft(Boolean.FALSE);
			rule.setStatus(Constants.PUBLISHED);
			rules.add(rule);
		}
		
		CopyExclusionRulesResponse response = new CopyExclusionRulesResponse();
		response.setData(rules);
		response.setMessage(Constants.COPY_SUCCESS);
		response.setCopiedCount(copiedExclusions.size());
		
		return response;
	}

	@Override
	@Transactional
	public void updateDraft(BigInteger companyId, BigInteger ruleId, CreateExclusionDraftRequest createDraftRequest) {
		
		ActiveFlag createDraft = null;
		
		if(createDraftRequest.getIsEnabled().equals(Boolean.TRUE)) {
			createDraft = ActiveFlag.Y;
		} else {
			createDraft = ActiveFlag.N;
		}
		
		CompanyCategoriesPreview companyCategoriesPreview = companyCategoriesPreviewRepository.findById(ruleId)
				.orElseThrow(() -> new CompanyCategoriesNotFoundException(ruleId));
		
		if(!companyCategoriesPreview.getIsEnabled().equals(createDraft)) {
			if(Boolean.FALSE.equals(companyCategoriesPreviewRepository.existsByCompanyAndCategoryAndIsEnabled(companyCategoriesPreview.getCompany(), companyCategoriesPreview.getCategory(), createDraft))) {
			CompanyCategoriesPreview draft = new CompanyCategoriesPreview();
			draft.setCompany(companyCategoriesPreview.getCompany());
			draft.setCategory(companyCategoriesPreview.getCategory());
			draft.setIsEnabled(createDraft);
			companyCategoriesPreviewRepository.save(draft);
			} else {
				companyCategoriesPreviewRepository.deleteById(ruleId);
			}
		} 
	}

	@Override
	public CompanyCustomParametersResponse getCompanyCustomParameters(BigInteger companyId, String search) {
		List<CompanyParameterProjection> result = companyParametersRepository.getCompanyCustomParameters(companyId,
				search);

		List<CompanyParameterDto> companyParameters = new ArrayList<>();

		for (CompanyParameterProjection projection : result) {
			CompanyParameterDto dto = new CompanyParameterDto();
			dto.setId(projection.getId());
			dto.setTitle(projection.getDescription());
			dto.setCode(projection.getParameterCode());
			dto.setVariableName(Constants.PREFIX_VARIABLE.concat(projection.getParameterCode().concat(Constants.SUFFIX_VARIABLE)));
			dto.setValue(projection.getParameterValue());
			if (projection.getParameterValue().equals(projection.getParameterValuePreview())) {
				dto.setHasDraft(Boolean.FALSE);
				dto.setStatus(Constants.PUBLISHED);
			} else {
				dto.setHasDraft(Boolean.TRUE);
				DraftCustomParameters draft = new DraftCustomParameters();
				draft.setStatus(Constants.DRAFT);
				draft.setValue(projection.getParameterValuePreview());
				dto.setDraft(draft);
				dto.setStatus(Constants.PENDING_PUBLICATION);
			}
			companyParameters.add(dto);
		}

		CompanyCustomParametersResponse response = new CompanyCustomParametersResponse();
		response.setData(companyParameters);

		return response;
	}

	@Override
	public void createCompanyParameterDraft(BigInteger companyId, BigInteger paramId,
			CreateParameterDraftRequest createDraftRequest) {
		CompanyParametersPreview companyParameters = companyParametersPreviewRepository.findById(paramId)
				.orElseThrow(() -> new CompanyParametersNotFoundException(paramId));

		if (!createDraftRequest.getValue().equals(companyParameters.getParameterValue())) {
			if (companyParametersPreviewRepository.countByCompanyAndCustomizationParameters(
					companyParameters.getCompany(), companyParameters.getCustomizationParameters()) == 1) {
				CompanyParametersPreview newDraft = new CompanyParametersPreview();
				newDraft.setCompany(companyParameters.getCompany());
				newDraft.setCustomizationParameters(companyParameters.getCustomizationParameters());
				newDraft.setParameterValue(createDraftRequest.getValue());
				companyParametersPreviewRepository.save(newDraft);
			} else if (Boolean.TRUE.equals(companyParametersPreviewRepository
					.existsByCompanyAndCustomizationParametersAndParameterValue(companyParameters.getCompany(),
							companyParameters.getCustomizationParameters(), createDraftRequest.getValue()))) {
				companyParametersPreviewRepository.deleteById(paramId);
			} else {
				companyParameters.setParameterValue(createDraftRequest.getValue());
				companyParametersPreviewRepository.save(companyParameters);
			}
		}
	}

	@Override
	@Transactional
	public void publishCompanyParameterDraft(BigInteger companyId, BigInteger paramId) {
		CompanyParametersPreview companyParametersPreviewToPublish = companyParametersPreviewRepository.findById(paramId)
				.orElseThrow(() -> new CompanyParametersPreviewNotFoundException(paramId));
		
		Optional<CompanyParameters> companyParameters = companyParametersRepository.findByCompanyAndCustomizationParameters(companyParametersPreviewToPublish.getCompany(), companyParametersPreviewToPublish.getCustomizationParameters());
		
		if(companyParameters.isPresent()) {
			companyParameters.get().setParameterValue(companyParametersPreviewToPublish.getParameterValue());
			companyParametersRepository.save(companyParameters.get());
		}
		
		Optional<CompanyParametersPreview> oldPublished = companyParametersPreviewRepository.findByCompanyAndCustomizationParametersAndIdNot(companyParametersPreviewToPublish.getCompany(), companyParametersPreviewToPublish.getCustomizationParameters(), paramId);
		if(oldPublished.isPresent()) {
			companyParametersPreviewRepository.delete(oldPublished.get());
		}
	}

	@Override
	@Transactional
	public CopyCustomParametersResponse copyCustomParameters(BigInteger companyId,
			CompanyCreateRequest copyExclusionRulesRequest) {
		Company company = companyRepository.findById(companyId)
				.orElseThrow(() -> new CompanyNotFoundException(companyId));
		companyParametersRepository.deleteByCompanyId(companyId);
		companyParametersPreviewRepository.deleteByCompanyId(companyId);
		
		List<CompanyParameters> parametersToCopy = companyParametersRepository.findParametersByCompanyId(copyExclusionRulesRequest.getSourceCompanyId());
		
		for(CompanyParameters parameterToCopy : parametersToCopy) {
			CompanyParameters companyParameter = new CompanyParameters();
			companyParameter.setCompany(company);
			companyParameter.setCustomizationParameters(parameterToCopy.getCustomizationParameters());
			companyParameter.setParameterValue(parameterToCopy.getParameterValue());
			companyParametersRepository.save(companyParameter);
			
			CompanyParametersPreview companyParameterPreview = new CompanyParametersPreview();
			companyParameterPreview.setCompany(company);
			companyParameterPreview.setCustomizationParameters(parameterToCopy.getCustomizationParameters());
			companyParameterPreview.setParameterValue(parameterToCopy.getParameterValue());
			companyParametersPreviewRepository.save(companyParameterPreview);
		}
		
		CopyCustomParametersResponse response = new CopyCustomParametersResponse();
		response.setData(getCompanyCustomParameters(companyId, null).getData());
		response.setCopiedCount(response.getData().size());
		response.setMessage(Constants.COPY_SUCCESS);
		
		return response;
	}

	@Override
	@Transactional
	public void publishExclusionRules(BigInteger companyId, BigInteger ruleId) {
		CompanyCategoriesPreview companyCategoriesPreviewToPublish = companyCategoriesPreviewRepository.findById(ruleId)
				.orElseThrow(() -> new CompanyCategoriesNotFoundException(ruleId));
		
		Optional<CompanyCategories> companyCategories = companyCategoriesRepository.findByCompanyAndCategory(companyCategoriesPreviewToPublish.getCompany(), companyCategoriesPreviewToPublish.getCategory());

		if(companyCategories.isPresent()) {
		companyCategories.get().setIsEnabled(companyCategoriesPreviewToPublish.getIsEnabled());
		companyCategoriesRepository.save(companyCategories.get());
		}
		
		Optional<CompanyCategoriesPreview> oldPublished = companyCategoriesPreviewRepository.findByCompanyAndCategoryAndIdNot(companyCategoriesPreviewToPublish.getCompany(), companyCategoriesPreviewToPublish.getCategory(), ruleId);
		if(oldPublished.isPresent()) {
			companyCategoriesPreviewRepository.delete(oldPublished.get());
		}
	}

	@Override
	@Transactional
	public CompanyParameterDto addCustomParameters(BigInteger companyId,
			AddCustomParametersRequest addCustomParametersRequest) {
		Company company = companyRepository.findById(companyId)
				.orElseThrow(() -> new CompanyNotFoundException(companyId));
		
		CustomizationParameters customParameter = customizationParametersRepository.findById(addCustomParametersRequest.getParameterCode())
				.orElseThrow(() -> new CustomizationParametersNotFoundException(addCustomParametersRequest.getParameterCode()));
		
		CompanyParameters companyParameters = new CompanyParameters();
		companyParameters.setCompany(company);
		companyParameters.setCustomizationParameters(customParameter);
		companyParameters.setParameterValue(addCustomParametersRequest.getValue());
		companyParametersRepository.save(companyParameters);
		
		CompanyParametersPreview companyParameterPreview = new CompanyParametersPreview();
		companyParameterPreview.setCompany(company);
		companyParameterPreview.setCustomizationParameters(customParameter);
		companyParameterPreview.setParameterValue(addCustomParametersRequest.getValue());
		companyParametersPreviewRepository.save(companyParameterPreview);
		
		CompanyParameterDto dto = new CompanyParameterDto();
		dto.setId(companyParameters.getId());
		dto.setTitle(companyParameters.getCustomizationParameters().getDescription());
		dto.setCode(companyParameters.getCustomizationParameters().getCode());
		dto.setVariableName(Constants.PREFIX_VARIABLE.concat(companyParameters.getCustomizationParameters().getCode().concat(Constants.SUFFIX_VARIABLE)));
		dto.setValue(companyParameters.getParameterValue());
		dto.setHasDraft(Boolean.FALSE);
		dto.setStatus(Constants.PUBLISHED);
		
		return dto;
	}
	
	
}