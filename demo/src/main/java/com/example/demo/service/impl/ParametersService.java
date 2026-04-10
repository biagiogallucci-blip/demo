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
import com.example.demo.entity.CompanyParameters;
import com.example.demo.entity.CompanyParametersPreview;
import com.example.demo.entity.CustomizationParameters;
import com.example.demo.handler.CompanyNotFoundException;
import com.example.demo.handler.CompanyParametersAlreadyExistException;
import com.example.demo.handler.CompanyParametersNotFoundException;
import com.example.demo.handler.CustomParametersAlreadyExistException;
import com.example.demo.handler.CustomizationParametersNotFoundException;
import com.example.demo.model.CompaniesWithCustomParameterDto;
import com.example.demo.model.CustomParametersDto;
import com.example.demo.model.DraftCustomParameters;
import com.example.demo.model.Meta;
import com.example.demo.model.Pagination;
import com.example.demo.model.ParametersDefinitionDto;
import com.example.demo.projection.CompaniesWithCustomParameterProjection;
import com.example.demo.projection.CompanyLookupProjection;
import com.example.demo.projection.CustomParameterListProjection;
import com.example.demo.repository.CompanyParametersPreviewRepository;
import com.example.demo.repository.CompanyParametersRepository;
import com.example.demo.repository.CompanyRepository;
import com.example.demo.repository.CustomizationParametersRepository;
import com.example.demo.request.CreateCustomParametersRequest;
import com.example.demo.request.SaveCustomParametersForCompanyRequest;
import com.example.demo.response.CompaniesWithCustomParameterResponse;
import com.example.demo.response.CreateCustomParametersResponse;
import com.example.demo.response.CustomParametersDetailsResponse;
import com.example.demo.response.CustomParametersResponse;
import com.example.demo.service.IParametersService;
import com.example.demo.utils.Constants;

@Service
public class ParametersService implements IParametersService {

	@Autowired
	private CustomizationParametersRepository customizationParametersRepository;

	@Autowired
	private CompanyRepository companyRepository;
	
	@Autowired
	private CompanyParametersRepository companyParametersRepository;
	
	@Autowired
	private CompanyParametersPreviewRepository companyParametersPreviewRepository;
	
	@Override
	public List<ParametersDefinitionDto> getParameterDefinitions() {
		List<CustomizationParameters> customizationParameters = customizationParametersRepository.findAll();

		List<ParametersDefinitionDto> response = new ArrayList<>();

		for (CustomizationParameters customParameter : customizationParameters) {
			ParametersDefinitionDto dto = new ParametersDefinitionDto();
			dto.setCode(customParameter.getCode());
			dto.setLabel(customParameter.getDescription());
			dto.setDataType(Constants.DATATYPE_TEXT);
			dto.setPlaceholder(
					Constants.PREFIX_VARIABLE.concat(customParameter.getCode().concat(Constants.SUFFIX_VARIABLE)));
			response.add(dto);
		}

		return response;
	}

	@Override
	public CustomParametersResponse getCustomParameters(Integer page, Integer limit, String search, String status) {
		Page<CustomParameterListProjection> result = customizationParametersRepository.getCustomParameters(search,
				status, PageRequest.of(page - 1, limit));

		List<CustomParametersDto> data = new ArrayList<>();

		for (CustomParameterListProjection projection : result.getContent()) {
			data.add(map(projection));
		}

		CustomParametersResponse response = new CustomParametersResponse();
		response.setData(data);

		Pagination pagination = new Pagination();
		pagination.setCurrentPage(page);
		pagination.setItemsPerPage(limit);
		pagination.setTotalPages(result.getTotalPages());
		pagination.setTotalItems(result.getTotalElements());

		Meta meta = new Meta();
		meta.setPagination(pagination);

		response.setMeta(meta);
		return response;
	}

	public CustomParametersDto map(CustomParameterListProjection p) {

		CustomParametersDto dto = new CustomParametersDto();

		dto.setId(p.getCode());
		dto.setTitle(p.getDescription());
		dto.setCode(p.getCode());
		dto.setPlaceholder(p.getPlaceholder());
		dto.setCompaniesCount(p.getCompaniesCount() != null ? p.getCompaniesCount() : 0);

		dto.setHasDraft(p.getHasDraft());
		dto.setStatus(p.getStatus());

		dto.setDetail(p.getDetailLink());

		return dto;
	}

	@Override
	@Transactional
	public CreateCustomParametersResponse createCustomParameter(
			CreateCustomParametersRequest createCustomParametersRequest) {
		customizationParametersRepository.findById(createCustomParametersRequest.getCode()).ifPresent(parameter -> {
			throw new CustomParametersAlreadyExistException(createCustomParametersRequest.getCode());
		});
		
		CustomizationParameters customParameter = new CustomizationParameters();
		customParameter.setCode(createCustomParametersRequest.getCode());
		customParameter.setDescription(createCustomParametersRequest.getTitle());
		customParameter.setId(customizationParametersRepository.getNextId());
		customizationParametersRepository.saveAndFlush(customParameter);
		
		List<Company> companies = new ArrayList<>();
		
		if (Boolean.TRUE.equals(createCustomParametersRequest.getApplyToAllCompanies())) {
			companies = companyRepository.findAll();
			for (Company company : companies) {
				CompanyParameters entity = new CompanyParameters();
				entity.setCompany(company);
				entity.setParameterCode(customParameter);
				entity.setParameterValue(createCustomParametersRequest.getValue());
				companyParametersRepository.save(entity);
				
				CompanyParametersPreview entityPreview = new CompanyParametersPreview();
				entityPreview.setCompany(company);
				entityPreview.setParameterCode(customParameter);
				entityPreview.setParameterValue(createCustomParametersRequest.getValue());
				companyParametersPreviewRepository.save(entityPreview);
				
			}
		}
		
		CreateCustomParametersResponse response = new CreateCustomParametersResponse();
		response.setId(customParameter.getId());
		response.setTitle(customParameter.getDescription());
		response.setCode(customParameter.getCode());
		response.setPlaceholder(Constants.PREFIX_VARIABLE.concat(customParameter.getCode()).concat(Constants.SUFFIX_VARIABLE));
		response.setStatus(Constants.PUBLISHED);
		response.setValue(createCustomParametersRequest.getValue());
		response.setCompaniesCount(companies.size());
		
		return response;
	}

	@Override
	public CustomParametersDetailsResponse getCustomParametersDetails(BigInteger paramId) {
		CustomizationParameters result = customizationParametersRepository.findById(paramId).orElseThrow(() -> new CustomizationParametersNotFoundException(paramId));
		
		CustomParametersDetailsResponse response = new CustomParametersDetailsResponse();
        response.setId(result.getId());
		response.setTitle(result.getDescription());
		response.setCode(result.getCode());
		response.setPlaceholder(Constants.PREFIX_VARIABLE.concat(result.getCode()).concat(Constants.SUFFIX_VARIABLE));
		
		return response;
	}

	@Override
	public CompaniesWithCustomParameterResponse getCompaniesWithCustomParameter(BigInteger paramId, String search,
			Integer page, Integer limit) {
		Page<CompaniesWithCustomParameterProjection> result = customizationParametersRepository.getCompaniesWithCustomParameter(paramId, search, PageRequest.of(page - 1, limit));
		
		List<CompaniesWithCustomParameterDto> data = new ArrayList<>();
		
		for(CompaniesWithCustomParameterProjection projection : result) {
			CompaniesWithCustomParameterDto dto = new CompaniesWithCustomParameterDto();

            dto.setCompanyId(projection.getCompanyId());
            dto.setCompanyName(projection.getCompanyName());
            dto.setCompanyCode(projection.getCompanyCode());
            dto.setValue(projection.getPublishedValue());
            boolean hasDraft = projection.getPreviewValue() != null &&
                               !projection.getPreviewValue().equals(projection.getPublishedValue());
            dto.setHasDraft(hasDraft);

            if (hasDraft) {
            	DraftCustomParameters draft = new DraftCustomParameters();
                draft.setValue(projection.getPreviewValue());
                draft.setStatus("DRAFT");

                dto.setDraft(draft);
                dto.setStatus("PENDING_PUBLICATION");
            } else {
                dto.setDraft(null);
                dto.setStatus("PUBLISHED");
            }
            data.add(dto);
		}
		
		CompaniesWithCustomParameterResponse response = new CompaniesWithCustomParameterResponse();
		response.setData(data);
		
		Pagination pagination = new Pagination();
		pagination.setCurrentPage(page);
		pagination.setItemsPerPage(limit);
		pagination.setTotalPages(result.getTotalPages());
		pagination.setTotalItems(result.getTotalElements());

		Meta meta = new Meta();
		meta.setPagination(pagination);

		response.setMeta(meta);
		
		return response;
	}

	@Override
	@Transactional
	public void deleteCustomParameters(BigInteger paramId, BigInteger companyId) {
		companyParametersPreviewRepository.deleteByCompanyAndParamId(companyId, paramId);
		companyParametersRepository.deleteByCompanyAndParamId(companyId, paramId);
	}

	@Override
	@Transactional
	public void publishCustomParameters(BigInteger paramId) {
		companyParametersRepository.updatePublishedFromDraft(paramId);
	    companyParametersPreviewRepository.deletePreview(paramId);
	    companyParametersPreviewRepository.insertPreview(paramId);	
	}

	@Override
	public List<CompanyLookupProjection> getCustomParametersLookup(BigInteger paramId) {
		return customizationParametersRepository.getCustomParametersLookup(paramId);
	}

	@Override
	@Transactional
	public void saveCustomParametersForCompany(BigInteger paramId,
			SaveCustomParametersForCompanyRequest saveCustomParametersForCompanyRequest) {
		Company company = companyRepository.findById(saveCustomParametersForCompanyRequest.getCompanyId())
				.orElseThrow(() -> new CompanyNotFoundException(saveCustomParametersForCompanyRequest.getCompanyId()));

		CustomizationParameters param = customizationParametersRepository.findById(paramId)
				.orElseThrow(() -> new CustomizationParametersNotFoundException(paramId));

		boolean exists = companyParametersRepository.existsByCompanyAndCustomizationParameters(company, param);

		boolean existsPreview = companyParametersPreviewRepository.existsByCompanyAndCustomizationParameters(company,
				param);

		if (exists || existsPreview) {
			throw new CompanyParametersAlreadyExistException(company.getIdCompany(), param.getId());
		}

		CompanyParameters cp = new CompanyParameters();
		cp.setCompany(company);
		cp.setParameterCode(param);
		cp.setParameterValue(saveCustomParametersForCompanyRequest.getValue());

		companyParametersRepository.save(cp);

		CompanyParametersPreview cpp = new CompanyParametersPreview();
		cpp.setCompany(company);
		cpp.setParameterCode(param);
		cpp.setParameterValue(saveCustomParametersForCompanyRequest.getValue());

		companyParametersPreviewRepository.save(cpp);
	}

	@Override
	@Transactional
	public void putCustomParametersValue(BigInteger paramId, BigInteger companyId, String value) {
		Company company = companyRepository.findById(companyId)
				.orElseThrow(() -> new CompanyNotFoundException(companyId));

		CustomizationParameters param = customizationParametersRepository.findById(paramId)
				.orElseThrow(() -> new CustomizationParametersNotFoundException(paramId));

		CompanyParameters companyParameters = companyParametersRepository
				.findByCompanyAndCustomizationParameters(company, param)
				.orElseThrow(() -> new CompanyParametersNotFoundException(paramId));

		List<CompanyParametersPreview> previews = companyParametersPreviewRepository
				.findByCompanyAndCustomizationParameters(company, param);

		if (companyParameters.getParameterValue().equals(value)) {
			previews.stream().filter(p -> !p.getParameterValue().equals(companyParameters.getParameterValue()))
					.forEach(companyParametersPreviewRepository::delete);

			return;
		}
		
	    Optional<CompanyParametersPreview> draftOpt =
	            previews.stream()
	                    .filter(p -> !p.getParameterValue().equals(companyParameters.getParameterValue()))
	                    .findFirst();

	    if (draftOpt.isPresent()) {
	        CompanyParametersPreview draft = draftOpt.get();
	        draft.setParameterValue(value);
	        companyParametersPreviewRepository.save(draft);

	    } else {
	        CompanyParametersPreview draft = new CompanyParametersPreview();
	        draft.setCompany(company);
	        draft.setParameterCode(param);
	        draft.setParameterValue(value);
	        companyParametersPreviewRepository.save(draft);
	    }
	}
}