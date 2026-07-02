package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
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
import com.example.demo.handler.CategoriesAlreadyExistException;
import com.example.demo.handler.CategoriesNotFoundException;
import com.example.demo.model.ExclusionRulesDto;
import com.example.demo.projection.CategoriesWithDraftProjection;
import com.example.demo.projection.CompaniesByExclusionRuleProjection;
import com.example.demo.projection.CompanyLookupProjection;
import com.example.demo.projection.ExclusionRulesProjection;
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
import com.example.demo.service.impl.ExclusionRulesService;
import com.example.demo.utils.Constants;

@ExtendWith(MockitoExtension.class)
class ExclusionRulesServiceTest {

	@Mock
	private CategoriesRepository categoriesRepository;
	@Mock
	private CompanyRepository companyRepository;
	@Mock
	private CompanyCategoriesRepository companyCategoriesRepository;
	@Mock
	private CompanyCategoriesPreviewRepository companyCategoriesPreviewRepository;
	@Mock
	private ICompaniesService companiesService;

	@InjectMocks
	private ExclusionRulesService service;

	@Test
	void shouldReturnExclusionRules_withDraftAndPublished() {
		Page<ExclusionRulesProjection> page = mock(Page.class);

		ExclusionRulesProjection draft = mock(ExclusionRulesProjection.class);
		when(draft.getId()).thenReturn(BigInteger.ONE);
		when(draft.getName()).thenReturn("Rule1");
		when(draft.getCode()).thenReturn("R1");
		when(draft.getCompanyCount()).thenReturn(2L);
		when(draft.getStatus()).thenReturn(Constants.DRAFT);

		ExclusionRulesProjection published = mock(ExclusionRulesProjection.class);
		when(published.getId()).thenReturn(BigInteger.TWO);
		when(published.getName()).thenReturn("Rule2");
		when(published.getCode()).thenReturn("R2");
		when(published.getCompanyCount()).thenReturn(1L);
		when(published.getStatus()).thenReturn(Constants.PUBLISHED);

		when(page.getContent()).thenReturn(List.of(draft, published));
		when(page.getTotalPages()).thenReturn(1);
		when(page.getTotalElements()).thenReturn(2L);

		when(categoriesRepository.getExclusionRules(any(), any(), any())).thenReturn(page);

		ExclusionRulesResponse response = service.getExclusionRules(1, 10, null, null);

		assertEquals(2, response.getData().size());
		assertTrue(response.getData().get(0).getHasDraft());
		assertFalse(response.getData().get(1).getHasDraft());
	}

	@Test
	void shouldCreateExclusionRule_withoutCompanies() {
		CreateExclusionRuleRequest request = new CreateExclusionRuleRequest();
		request.setTag("TAG");
		request.setName("NAME");
		request.setApplyToAllCompanies(false);

		when(categoriesRepository.findById("TAG")).thenReturn(Optional.empty());
		when(categoriesRepository.getNextId()).thenReturn(BigInteger.ONE);

		CreateExclusionRuleResponse response = service.createExclusionRule(request);

		verify(categoriesRepository).saveAndFlush(any());
		assertEquals(0, response.getCompaniesCount());
	}

	@Test
	void shouldCreateExclusionRule_withAllCompanies() {
		CreateExclusionRuleRequest request = new CreateExclusionRuleRequest();
		request.setTag("TAG");
		request.setName("NAME");
		request.setApplyToAllCompanies(true);

		when(categoriesRepository.findById("TAG")).thenReturn(Optional.empty());

		when(categoriesRepository.getNextId()).thenReturn(BigInteger.ONE);

		when(companyRepository.count()).thenReturn(1L);

		CreateExclusionRuleResponse response = service.createExclusionRule(request);

		verify(categoriesRepository).saveAndFlush(any());
		verify(companyCategoriesRepository).insertCategoriesForAllCompanies("TAG");
		verify(companyCategoriesPreviewRepository).insertCategoriesPreviewForAllCompanies("TAG");

		assertEquals(1L, response.getCompaniesCount());
	}

	@Test
	void shouldThrowWhenCategoryAlreadyExists() {
		CreateExclusionRuleRequest request = new CreateExclusionRuleRequest();
		request.setTag("TAG");

		when(categoriesRepository.findById("TAG")).thenReturn(Optional.of(new Categories()));

		assertThrows(CategoriesAlreadyExistException.class, () -> service.createExclusionRule(request));
	}

	@Test
	void shouldReturnExclusionRuleById_withDraft() {
		Categories category = new Categories();
		category.setId(BigInteger.ONE);
		category.setName("NAME");
		category.setCode("TAG");

		CategoriesWithDraftProjection projection = mock(CategoriesWithDraftProjection.class);
		when(projection.getCategory()).thenReturn(category);
		when(projection.getStatus()).thenReturn(Constants.DRAFT);

		when(categoriesRepository.getByRuleId(BigInteger.ONE)).thenReturn(Optional.of(projection));

		ExclusionRulesDto response = service.getExclusionRuleById(BigInteger.ONE);

		assertTrue(response.getHasDraft());
	}

	@Test
	void shouldThrowWhenRuleNotFound() {
		when(categoriesRepository.getByRuleId(any())).thenReturn(Optional.empty());

		assertThrows(CategoriesNotFoundException.class, () -> service.getExclusionRuleById(BigInteger.ONE));
	}

	@Test
	void shouldReturnCompaniesByRule_withDraft() {
		Page<CompaniesByExclusionRuleProjection> page = mock(Page.class);
		CompaniesByExclusionRuleProjection projection = mock(CompaniesByExclusionRuleProjection.class);

		when(projection.getCompanyId()).thenReturn(BigInteger.ONE);
		when(projection.getCompanyName()).thenReturn("Company");
		when(projection.getCompanyCode()).thenReturn("C1");
		when(projection.getStatus()).thenReturn("DRAFT");

		when(page.getContent()).thenReturn(List.of(projection));
		when(page.getTotalPages()).thenReturn(1);
		when(page.getTotalElements()).thenReturn(1L);

		when(companyCategoriesRepository.getCompaniesByExclusionRuleId(any(), any(), any())).thenReturn(page);

		CompaniesByExclusionRuleResponse response = service.getCompaniesByExclusionRuleId(BigInteger.ONE, 1, 10, null);

		assertTrue(response.getData().get(0).getHasDraft());
		assertNotNull(response.getData().get(0).getDraft());
	}

	@Test
	void shouldReturnCompaniesByRule_withoutDraft() {
		Page<CompaniesByExclusionRuleProjection> page = mock(Page.class);
		CompaniesByExclusionRuleProjection projection = mock(CompaniesByExclusionRuleProjection.class);

		when(projection.getCompanyId()).thenReturn(BigInteger.ONE);
		when(projection.getCompanyName()).thenReturn("Company");
		when(projection.getCompanyCode()).thenReturn("C1");

		when(page.getContent()).thenReturn(List.of(projection));
		when(page.getTotalPages()).thenReturn(1);
		when(page.getTotalElements()).thenReturn(1L);

		when(companyCategoriesRepository.getCompaniesByExclusionRuleId(any(), any(), any())).thenReturn(page);

		CompaniesByExclusionRuleResponse response = service.getCompaniesByExclusionRuleId(BigInteger.ONE, 1, 10, null);

		assertFalse(response.getData().get(0).getHasDraft());
	}

	@Test
	void shouldReturnExclusionRuleLookup() {
		when(companyRepository.getExclusionRuleLookup(any())).thenReturn(List.of(mock(CompanyLookupProjection.class)));

		List<CompanyLookupProjection> result = service.getExclusionRuleLookup(BigInteger.ONE);

		assertEquals(1, result.size());
	}

	@Test
	void shouldCreateExclusionRuleForCompany() {
		Categories category = new Categories();

		CategoriesWithDraftProjection projection = mock(CategoriesWithDraftProjection.class);
		when(projection.getCategory()).thenReturn(category);

		when(categoriesRepository.getByRuleId(any())).thenReturn(Optional.of(projection));

		Company company = new Company();
		when(companyRepository.findById(any())).thenReturn(Optional.of(company));

		CompanyCreateRequest request = new CompanyCreateRequest();
		request.setSourceCompanyId(BigInteger.ONE);

		service.createExclusionRuleForCompany(BigInteger.ONE, request);

		verify(companyCategoriesRepository).save(any());
		verify(companyCategoriesPreviewRepository).save(any());
	}

	@Test
	void shouldCloneExclusionRule() {
		BigInteger ruleId = BigInteger.valueOf(10);

		CloneExclusionRuleRequest request = new CloneExclusionRuleRequest();
		request.setNewTag("NEW_TAG");
		request.setNewName("New Rule");

		Categories source = new Categories();
		source.setCode("OLD_TAG");
		source.setId(ruleId);

		when(categoriesRepository.findById("NEW_TAG")).thenReturn(Optional.empty());

		when(categoriesRepository.findByBusinessId(ruleId)).thenReturn(Optional.of(source));

		when(categoriesRepository.getNextId()).thenReturn(BigInteger.valueOf(999));

		CloneExclusionRuleResponse response = service.cloneExclusionRule(ruleId, request);

		assertNotNull(response);
		assertEquals(BigInteger.valueOf(999), response.getId());
		assertEquals("New Rule", response.getName());

		verify(categoriesRepository).findById("NEW_TAG");
		verify(categoriesRepository).findByBusinessId(ruleId);
		verify(categoriesRepository).getNextId();
		verify(categoriesRepository).saveAndFlush(any(Categories.class));

		verify(companyCategoriesRepository).cloneCompanyCategories("OLD_TAG", "NEW_TAG");

		verify(companyCategoriesPreviewRepository).cloneCompanyCategoriesPreview("OLD_TAG", "NEW_TAG");

		verifyNoMoreInteractions(categoriesRepository, companyCategoriesRepository, companyCategoriesPreviewRepository);
	}

	@Test
	void shouldPublishExclusionRule() {
		BigInteger ruleId = BigInteger.ONE;
		List<BigInteger> ids = List.of(BigInteger.ONE, BigInteger.TWO, BigInteger.valueOf(3));

		when(companyCategoriesPreviewRepository.findExclusionRuleToPublish(ruleId)).thenReturn(ids);
		service.publishExclusionRule(ruleId);

		verify(companyCategoriesPreviewRepository).findExclusionRuleToPublish(ruleId);
		verify(companyCategoriesRepository).deleteAllById(ids);
		verifyNoMoreInteractions(companyCategoriesRepository, companyCategoriesPreviewRepository);
	}
}