package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
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
import com.example.demo.service.impl.ExclusionRulesService;
import com.example.demo.utils.ActiveFlag;
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
        when(draft.getCompanyCount()).thenReturn(2);
        when(draft.getStatus()).thenReturn(Constants.DRAFT);

        ExclusionRulesProjection published = mock(ExclusionRulesProjection.class);
        when(published.getId()).thenReturn(BigInteger.TWO);
        when(published.getName()).thenReturn("Rule2");
        when(published.getCode()).thenReturn("R2");
        when(published.getCompanyCount()).thenReturn(1);
        when(published.getStatus()).thenReturn(Constants.PUBLISHED);

        when(page.getContent()).thenReturn(List.of(draft, published));
        when(page.getTotalPages()).thenReturn(1);
        when(page.getTotalElements()).thenReturn(2L);

        when(categoriesRepository.getExclusionRules(any(), any(), any()))
                .thenReturn(page);

        ExclusionRulesResponse response =
                service.getExclusionRules(1, 10, null, null);

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

        CreateExclusionRuleResponse response =
                service.createExclusionRule(request);

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

        Company company = new Company();
        when(companyRepository.findAll()).thenReturn(List.of(company));

        CreateExclusionRuleResponse response =
                service.createExclusionRule(request);

        verify(companyCategoriesRepository).save(any());
        verify(companyCategoriesPreviewRepository).save(any());
        assertEquals(1, response.getCompaniesCount());
    }

    @Test
    void shouldThrowWhenCategoryAlreadyExists() {
        CreateExclusionRuleRequest request = new CreateExclusionRuleRequest();
        request.setTag("TAG");

        when(categoriesRepository.findById("TAG"))
                .thenReturn(Optional.of(new Categories()));

        assertThrows(CategoriesAlreadyExistException.class,
                () -> service.createExclusionRule(request));
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

        when(categoriesRepository.getByRuleId(BigInteger.ONE))
                .thenReturn(Optional.of(projection));

        ExclusionRulesDto response =
                service.getExclusionRuleById(BigInteger.ONE);

        assertTrue(response.getHasDraft());
    }

    @Test
    void shouldThrowWhenRuleNotFound() {
        when(categoriesRepository.getByRuleId(any()))
                .thenReturn(Optional.empty());

        assertThrows(CategoriesNotFoundException.class,
                () -> service.getExclusionRuleById(BigInteger.ONE));
    }

    @Test
    void shouldReturnCompaniesByRule_withDraft() {
        Page<CompaniesByExclusionRuleProjection> page = mock(Page.class);
        CompaniesByExclusionRuleProjection projection = mock(CompaniesByExclusionRuleProjection.class);

        when(projection.getCompanyId()).thenReturn(BigInteger.ONE);
        when(projection.getCompanyName()).thenReturn("Company");
        when(projection.getCompanyCode()).thenReturn("C1");
        when(projection.getIsEnabled()).thenReturn("Y");
        when(projection.getDraftIsEnabled()).thenReturn("N");

        when(page.getContent()).thenReturn(List.of(projection));
        when(page.getTotalPages()).thenReturn(1);
        when(page.getTotalElements()).thenReturn(1L);

        when(companyCategoriesRepository.getCompaniesByExclusionRuleId(any(), any(), any()))
                .thenReturn(page);

        CompaniesByExclusionRuleResponse response =
                service.getCompaniesByExclusionRuleId(BigInteger.ONE, 1, 10, null);

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
        when(projection.getIsEnabled()).thenReturn("Y");
        when(projection.getDraftIsEnabled()).thenReturn("Y");

        when(page.getContent()).thenReturn(List.of(projection));
        when(page.getTotalPages()).thenReturn(1);
        when(page.getTotalElements()).thenReturn(1L);

        when(companyCategoriesRepository.getCompaniesByExclusionRuleId(any(), any(), any()))
                .thenReturn(page);

        CompaniesByExclusionRuleResponse response =
                service.getCompaniesByExclusionRuleId(BigInteger.ONE, 1, 10, null);

        assertFalse(response.getData().get(0).getHasDraft());
    }

    @Test
    void shouldReturnExclusionRuleLookup() {
        when(companyRepository.getExclusionRuleLookup(any()))
                .thenReturn(List.of(mock(CompanyLookupProjection.class)));

        List<CompanyLookupProjection> result =
                service.getExclusionRuleLookup(BigInteger.ONE);

        assertEquals(1, result.size());
    }

    @Test
    void shouldCreateExclusionRuleForCompany() {
        Categories category = new Categories();

        CategoriesWithDraftProjection projection = mock(CategoriesWithDraftProjection.class);
        when(projection.getCategory()).thenReturn(category);

        when(categoriesRepository.getByRuleId(any()))
                .thenReturn(Optional.of(projection));

        Company company = new Company();
        when(companyRepository.findById(any()))
                .thenReturn(Optional.of(company));

        CompanyCreateRequest request = new CompanyCreateRequest();
        request.setSourceCompanyId(BigInteger.ONE);

        service.createExclusionRuleForCompany(BigInteger.ONE, request);

        verify(companyCategoriesRepository).save(any());
        verify(companyCategoriesPreviewRepository).save(any());
    }

    @Test
    void shouldCloneExclusionRule() {
        when(categoriesRepository.getNextId())
                .thenReturn(BigInteger.ONE);

        CloneExclusionRuleProjection projection = mock(CloneExclusionRuleProjection.class);
        when(projection.getCompany()).thenReturn(new Company());
        when(projection.getIsEnabled()).thenReturn(ActiveFlag.Y);

        when(companyCategoriesRepository.findForCloneExclusionRule(any()))
                .thenReturn(List.of(projection));

        CloneExclusionRuleRequest request = new CloneExclusionRuleRequest();
        request.setNewName("NEW");
        request.setNewTag("NEW_TAG");

        CloneExclusionRuleResponse response =
                service.cloneExclusionRule(BigInteger.ONE, request);

        verify(companyCategoriesRepository).save(any());
        verify(companyCategoriesPreviewRepository).save(any());

        assertEquals("NEW", response.getName());
    }
    
    @Test
    void shouldPublishExclusionRule() {
        PublishExclusionRuleProjection projection = mock(PublishExclusionRuleProjection.class);

        when(projection.getCompanyId()).thenReturn(BigInteger.ONE);
        when(projection.getCompanyCategoriesPreviewId()).thenReturn(BigInteger.TEN);

        when(companyCategoriesPreviewRepository.findExclusionRuleToPublish(any()))
                .thenReturn(List.of(projection));

        service.publishExclusionRule(BigInteger.ONE);

        verify(companiesService)
                .publishExclusionRules(BigInteger.ONE, BigInteger.TEN);
    }
}