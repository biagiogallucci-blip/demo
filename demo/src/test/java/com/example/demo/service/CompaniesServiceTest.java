package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import com.example.demo.entity.Categories;
import com.example.demo.entity.Company;
import com.example.demo.entity.CompanyParameters;
import com.example.demo.entity.CompanyParametersPreview;
import com.example.demo.entity.CustomizationParameters;
import com.example.demo.handler.CompanyNotFoundException;
import com.example.demo.model.CompanyExclusionRulesDto;
import com.example.demo.model.CompanyParameterDto;
import com.example.demo.projection.CompanyExclusionRulesProjection;
import com.example.demo.projection.CompanyLookupProjection;
import com.example.demo.projection.CompanyParameterProjection;
import com.example.demo.projection.CompanyProjection;
import com.example.demo.repository.CategoriesRepository;
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
import com.example.demo.service.impl.CompaniesService;
import com.example.demo.utils.Constants;

@ExtendWith(MockitoExtension.class)
class CompaniesServiceTest {

	@Mock
	private CompanyRepository companyRepository;
	@Mock
	private CompanyCategoriesRepository companyCategoriesRepository;
	@Mock
	private CompanyCategoriesPreviewRepository companyCategoriesPreviewRepository;
	@Mock
	private CompanyParametersRepository companyParametersRepository;
	@Mock
	private CompanyParametersPreviewRepository companyParametersPreviewRepository;
	@Mock
	private CustomizationParametersRepository customizationParametersRepository;
	@Mock
	private CategoriesRepository categoriesRepository;

	@InjectMocks
	private CompaniesService service;

	@Test
	void shouldReturnCompanyDetails() {
		BigInteger id = BigInteger.ONE;

		Company company = new Company();
		company.setIdCompany(id);
		company.setNameCompany("Test");
		company.setCodeCompany("TST");

		when(companyRepository.findById(id)).thenReturn(Optional.of(company));

		CompanyDetailsResponse response = service.getCompanyDetails(id);

		assertEquals("Test", response.getName());
		verify(companyRepository).findById(id);
	}

	@Test
	void shouldThrowWhenCompanyNotFound() {
		BigInteger id = BigInteger.ONE;

		when(companyRepository.findById(id)).thenReturn(Optional.empty());

		assertThrows(CompanyNotFoundException.class, () -> service.getCompanyDetails(id));
	}

	@Test
	void shouldReturnCompaniesPage() {
		Page<CompanyProjection> page = mock(Page.class);
		CompanyProjection projection = mock(CompanyProjection.class);

		when(projection.getIdCompany()).thenReturn(BigInteger.ONE);
		when(projection.getNameCompany()).thenReturn("Name");
		when(projection.getCodeCompany()).thenReturn("Code");
		when(projection.getHasParameters()).thenReturn(true);
		when(projection.getHasCategory()).thenReturn(true);

		when(page.getContent()).thenReturn(List.of(projection));
		when(page.getTotalPages()).thenReturn(1);
		when(page.getTotalElements()).thenReturn(1L);

		when(companyRepository.searchCompanies(any(), any())).thenReturn(page);

		CompaniesResponse response = service.getCompanies(1, 10, null);

		assertEquals(1, response.getData().size());
		verify(companyRepository).searchCompanies(any(), any());
	}

	@Test
	void shouldMapExclusionRules() {
		CompanyExclusionRulesProjection projection = mock(CompanyExclusionRulesProjection.class);

		when(projection.getId()).thenReturn(BigInteger.ONE);
		when(projection.getName()).thenReturn("Cat");
		when(projection.getTag()).thenReturn("TAG");
		when(projection.getIsEnabled()).thenReturn(1);
		when(projection.getHasDraft()).thenReturn(0);
		when(projection.getStatus()).thenReturn(Constants.PUBLISHED);

		when(companyCategoriesRepository.getCompanyExclusionRules(BigInteger.ONE, null))
				.thenReturn(List.of(projection));

		CompanyExclusionRulesResponse response = service.getCompanyExclusionRules(BigInteger.ONE, null);

		assertNotNull(response);
		assertEquals(1, response.getData().size());

		CompanyExclusionRulesDto dto = response.getData().get(0);

		assertEquals(BigInteger.ONE, dto.getId());
		assertEquals("Cat", dto.getName());
		assertEquals("TAG", dto.getTag());
		assertTrue(dto.getIsEnabled());
		assertFalse(dto.getHasDraft());
		assertEquals(Constants.PUBLISHED, dto.getStatus());
		assertNull(dto.getDraft());

		verify(companyCategoriesRepository).getCompanyExclusionRules(BigInteger.ONE, null);
	}

	@Test
	void shouldCopyCustomParameters() {
		BigInteger companyId = BigInteger.ONE;
		BigInteger sourceCompanyId = BigInteger.TWO;

		Company company = new Company();
		when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));

		CustomizationParameters customizationParameters = new CustomizationParameters();
		customizationParameters.setCode("CODE");
		customizationParameters.setDescription("DESC");

		CompanyParameters parameter = new CompanyParameters();
		parameter.setCustomizationParameters(customizationParameters);
		parameter.setParameterValue("VAL");

		when(companyParametersRepository.findParametersByCompanyId(sourceCompanyId)).thenReturn(List.of(parameter));

		CompanyParameterProjection projection = mock(CompanyParameterProjection.class);
		when(projection.getId()).thenReturn(BigInteger.ONE);
		when(projection.getDescription()).thenReturn("DESC");
		when(projection.getParameterCode()).thenReturn("CODE");
		when(projection.getParameterValue()).thenReturn("VAL");
		when(projection.getParameterValuePreview()).thenReturn("VAL");

		when(companyParametersRepository.getCompanyCustomParameters(companyId, null)).thenReturn(List.of(projection));

		CompanyCreateRequest request = new CompanyCreateRequest();
		request.setSourceCompanyId(sourceCompanyId);

		CopyCustomParametersResponse response = service.copyCustomParameters(companyId, request);

		verify(companyParametersRepository).deleteByCompanyId(companyId);
		verify(companyParametersPreviewRepository).deleteByCompanyId(companyId);
		verify(companyParametersRepository).save(any());
		verify(companyParametersPreviewRepository).save(any());

		assertNotNull(response);
		assertEquals(1, response.getCopiedCount());
		assertEquals(Constants.COPY_SUCCESS, response.getMessage());
		assertEquals(1, response.getData().size());
	}

	@Test
	void shouldCreateDraftWhenDifferent() {
		BigInteger companyId = BigInteger.ONE;
		BigInteger previewId = BigInteger.ONE;
		BigInteger ruleId = BigInteger.ONE;

		Categories ruleCategory = mock(Categories.class);

		when(categoriesRepository.findByBusinessId(ruleId)).thenReturn(Optional.of(ruleCategory));

		when(ruleCategory.getCode()).thenReturn("TAG");

		when(companyCategoriesPreviewRepository.existsPreview(companyId, "TAG")).thenReturn(0L);

		CreateExclusionDraftRequest request = new CreateExclusionDraftRequest();
		request.setIsEnabled(true);

		service.updateDraft(companyId, previewId, request);

		verify(companyCategoriesPreviewRepository).insertPreview(companyId, "TAG");
	}

	@Test
	void shouldReturnCustomParameters() {
		CompanyParameterProjection projection = mock(CompanyParameterProjection.class);

		when(projection.getId()).thenReturn(BigInteger.ONE);
		when(projection.getDescription()).thenReturn("desc");
		when(projection.getParameterCode()).thenReturn("CODE");
		when(projection.getParameterValue()).thenReturn("A");
		when(projection.getParameterValuePreview()).thenReturn("A");

		when(companyParametersRepository.getCompanyCustomParameters(any(), any())).thenReturn(List.of(projection));

		CompanyCustomParametersResponse response = service.getCompanyCustomParameters(BigInteger.ONE, null);

		assertEquals(1, response.getData().size());
	}

	@Test
	void shouldCreateNewDraft() {
		CompanyParametersPreview preview = mock(CompanyParametersPreview.class);

		when(companyParametersPreviewRepository.findById(any())).thenReturn(Optional.of(preview));

		when(preview.getParameterValue()).thenReturn("OLD");
		when(preview.getCompany()).thenReturn(mock(Company.class));
		when(preview.getCustomizationParameters()).thenReturn(mock(CustomizationParameters.class));

		when(companyParametersPreviewRepository.countByCompanyAndCustomizationParameters(any(), any())).thenReturn(1);

		CreateParameterDraftRequest request = new CreateParameterDraftRequest();
		request.setValue("NEW");

		service.createCompanyParameterDraft(BigInteger.ONE, BigInteger.ONE, request);

		verify(companyParametersPreviewRepository).save(any());
	}

	@Test
	void shouldPublishParameterDraft() {
		CompanyParametersPreview preview = mock(CompanyParametersPreview.class);

		when(companyParametersPreviewRepository.findById(any())).thenReturn(Optional.of(preview));

		when(preview.getCompany()).thenReturn(mock(Company.class));
		when(preview.getCustomizationParameters()).thenReturn(mock(CustomizationParameters.class));
		when(preview.getParameterValue()).thenReturn("VAL");

		CompanyParameters parameter = new CompanyParameters();

		when(companyParametersRepository.findByCompanyAndCustomizationParameters(any(), any()))
				.thenReturn(Optional.of(parameter));

		service.publishCompanyParameterDraft(BigInteger.ONE, BigInteger.ONE);

		verify(companyParametersRepository).save(any());
	}

	@Test
	void shouldPublishExclusionRule() {
		BigInteger companyId = BigInteger.ONE;
		BigInteger ruleId = BigInteger.ONE;

		Categories category = mock(Categories.class);
		when(category.getCode()).thenReturn("TAG");

		when(categoriesRepository.findByBusinessId(ruleId)).thenReturn(Optional.of(category));

		when(companyCategoriesRepository.existsCompanyCategory(companyId, "TAG")).thenReturn(1L);

		when(companyCategoriesPreviewRepository.existsPreview(companyId, "TAG")).thenReturn(0L);

		service.publishExclusionRules(companyId, ruleId);

		verify(companyCategoriesRepository).deleteCompanyCategory(companyId, "TAG");

		verify(companyCategoriesPreviewRepository, never()).insertPreview(any(), any());
	}

	@Test
	void shouldAddCustomParameter() {
		BigInteger companyId = BigInteger.ONE;

		Company company = new Company();
		CustomizationParameters param = new CustomizationParameters();
		param.setCode("PARAM_1");
		param.setDescription("desc");

		when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));

		when(customizationParametersRepository.findById(any(String.class))).thenReturn(Optional.of(param));

		AddCustomParametersRequest request = new AddCustomParametersRequest();
		request.setParameterCode("PARAM_1");
		request.setValue("VAL");

		CompanyParameterDto dto = service.addCustomParameters(companyId, request);

		assertNotNull(dto);
		assertEquals("PARAM_1", dto.getCode());

		verify(companyParametersRepository).save(any());
		verify(companyParametersPreviewRepository).save(any());
	}

	@Test
	void shouldMapExclusionRules_withDraft_whenValuesAreDifferent() {
		BigInteger companyId = BigInteger.ONE;

		CompanyExclusionRulesProjection projection = mock(CompanyExclusionRulesProjection.class);

		when(projection.getId()).thenReturn(BigInteger.ONE);
		when(projection.getName()).thenReturn("Category");
		when(projection.getTag()).thenReturn("CAT");

		when(projection.getIsEnabled()).thenReturn(1);
		when(projection.getHasDraft()).thenReturn(1);
		when(projection.getDraftIsEnabled()).thenReturn(0);
		when(projection.getStatus()).thenReturn(Constants.PENDING_PUBLICATION);

		when(companyCategoriesRepository.getCompanyExclusionRules(companyId, null)).thenReturn(List.of(projection));

		CompanyExclusionRulesResponse response = service.getCompanyExclusionRules(companyId, null);

		assertNotNull(response);
		assertEquals(1, response.getData().size());

		CompanyExclusionRulesDto dto = response.getData().get(0);

		assertEquals(BigInteger.ONE, dto.getId());
		assertEquals("Category", dto.getName());
		assertEquals("CAT", dto.getTag());

		assertTrue(dto.getIsEnabled());
		assertTrue(dto.getHasDraft());
		assertEquals(Constants.PENDING_PUBLICATION, dto.getStatus());

		assertNotNull(dto.getDraft());
		assertEquals(Constants.DRAFT, dto.getDraft().getStatus());
		assertFalse(dto.getDraft().getIsEnabled());

		verify(companyCategoriesRepository).getCompanyExclusionRules(companyId, null);
	}

	@Test
	void shouldReturnCompanyLookup() {
		BigInteger companyId = BigInteger.ONE;

		CompanyLookupProjection projection = mock(CompanyLookupProjection.class);

		List<CompanyLookupProjection> expectedList = List.of(projection);

		when(companyRepository.getCompanyLookup(companyId)).thenReturn(expectedList);

		List<CompanyLookupProjection> result = service.getCompanyLookup(companyId);

		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(expectedList, result);

		verify(companyRepository).getCompanyLookup(companyId);
	}

	@Test
	void shouldCopyExclusionRules() {

		BigInteger companyId = BigInteger.ONE;
	    BigInteger sourceCompanyId = BigInteger.TWO;

	    CompanyCreateRequest request = new CompanyCreateRequest();
	    request.setSourceCompanyId(sourceCompanyId);

	    Company company = mock(Company.class);

	    when(companyRepository.findById(companyId))
	            .thenReturn(Optional.of(company));

	    doNothing().when(companyCategoriesRepository).deleteByCompanyId(companyId);
	    doNothing().when(companyCategoriesPreviewRepository).deleteByCompanyId(companyId);

	    when(companyCategoriesRepository.copyCompanyCategories(sourceCompanyId, companyId))
	            .thenReturn(3);

	    doNothing().when(companyCategoriesPreviewRepository)
	            .copyCompanyCategoriesPreview(sourceCompanyId, companyId);

	    CompanyExclusionRulesResponse rulesResponse = new CompanyExclusionRulesResponse();
	    rulesResponse.setData(new ArrayList<>());

	    when(companyCategoriesRepository.getCompanyExclusionRules(companyId, null))
	            .thenReturn(new ArrayList<>());

	    CopyExclusionRulesResponse response =
	            service.copyExclusionRules(companyId, request);

	    verify(companyRepository).findById(companyId);

	    verify(companyCategoriesRepository).deleteByCompanyId(companyId);
	    verify(companyCategoriesPreviewRepository).deleteByCompanyId(companyId);

	    verify(companyCategoriesRepository)
	            .copyCompanyCategories(sourceCompanyId, companyId);

	    verify(companyCategoriesPreviewRepository)
	            .copyCompanyCategoriesPreview(sourceCompanyId, companyId);

	    verify(companyCategoriesRepository)
	            .getCompanyExclusionRules(companyId, null);

	    assertEquals(Constants.COPY_SUCCESS, response.getMessage());
	    assertEquals(3, response.getCopiedCount());
	    assertNotNull(response.getData());
	}
}