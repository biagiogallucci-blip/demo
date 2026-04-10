package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
import org.springframework.data.domain.PageImpl;

import com.example.demo.entity.Company;
import com.example.demo.entity.CompanyParameters;
import com.example.demo.entity.CompanyParametersPreview;
import com.example.demo.entity.CustomizationParameters;
import com.example.demo.handler.CompanyParametersAlreadyExistException;
import com.example.demo.handler.CustomParametersAlreadyExistException;
import com.example.demo.handler.CustomizationParametersNotFoundException;
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
import com.example.demo.service.impl.ParametersService;

@ExtendWith(MockitoExtension.class)
class ParametersServiceTest {

    @Mock
    private CustomizationParametersRepository customizationParametersRepository;
    @Mock
    private CompanyRepository companyRepository;
    @Mock
    private CompanyParametersRepository companyParametersRepository;
    @Mock
    private CompanyParametersPreviewRepository companyParametersPreviewRepository;

    @InjectMocks
    private ParametersService service;

    @Test
    void shouldReturnParameterDefinitions() {
        CustomizationParameters param = new CustomizationParameters();
        param.setCode("CODE");
        param.setDescription("DESC");

        when(customizationParametersRepository.findAll())
                .thenReturn(List.of(param));

        List<ParametersDefinitionDto> result =
                service.getParameterDefinitions();

        assertEquals(1, result.size());
        assertEquals("CODE", result.get(0).getCode());
    }

    @Test
    void shouldReturnCustomParameters() {
        Page<CustomParameterListProjection> page = mock(Page.class);
        CustomParameterListProjection projection = mock(CustomParameterListProjection.class);

        when(projection.getCode()).thenReturn("CODE");
        when(projection.getDescription()).thenReturn("DESC");
        when(projection.getPlaceholder()).thenReturn("PH");
        when(projection.getCompaniesCount()).thenReturn(1);
        when(projection.getHasDraft()).thenReturn(true);
        when(projection.getStatus()).thenReturn("DRAFT");
        when(projection.getDetailLink()).thenReturn("LINK");

        when(page.getContent()).thenReturn(List.of(projection));
        when(page.getTotalPages()).thenReturn(1);
        when(page.getTotalElements()).thenReturn(1L);

        when(customizationParametersRepository.getCustomParameters(any(), any(), any()))
                .thenReturn(page);

        CustomParametersResponse response =
                service.getCustomParameters(1, 10, null, null);

        assertEquals(1, response.getData().size());
    }

    @Test
    void shouldCreateCustomParameter_withoutCompanies() {
        CreateCustomParametersRequest request = new CreateCustomParametersRequest();
        request.setCode("CODE");
        request.setTitle("TITLE");
        request.setValue("VAL");
        request.setApplyToAllCompanies(false);

        when(customizationParametersRepository.findById("CODE"))
                .thenReturn(Optional.empty());

        when(customizationParametersRepository.getNextId())
                .thenReturn(BigInteger.ONE);

        CreateCustomParametersResponse response =
                service.createExclusionRule(request);

        verify(customizationParametersRepository).saveAndFlush(any());
        assertEquals(0, response.getCompaniesCount());
    }

    @Test
    void shouldCreateCustomParameter_withCompanies() {
        CreateCustomParametersRequest request = new CreateCustomParametersRequest();
        request.setCode("CODE");
        request.setTitle("TITLE");
        request.setValue("VAL");
        request.setApplyToAllCompanies(true);

        when(customizationParametersRepository.findById("CODE"))
                .thenReturn(Optional.empty());

        when(customizationParametersRepository.getNextId())
                .thenReturn(BigInteger.ONE);

        Company company = new Company();
        when(companyRepository.findAll()).thenReturn(List.of(company));

        CreateCustomParametersResponse response =
                service.createExclusionRule(request);

        verify(companyParametersRepository).save(any());
        verify(companyParametersPreviewRepository).save(any());

        assertEquals(1, response.getCompaniesCount());
    }

    @Test
    void shouldThrowWhenCustomParameterAlreadyExists() {
        CreateCustomParametersRequest request = new CreateCustomParametersRequest();
        request.setCode("CODE");

        when(customizationParametersRepository.findById("CODE"))
                .thenReturn(Optional.of(new CustomizationParameters()));

        assertThrows(CustomParametersAlreadyExistException.class,
                () -> service.createExclusionRule(request));
    }

    @Test
    void shouldReturnCustomParameterDetails() {
        CustomizationParameters param = new CustomizationParameters();
        param.setId(BigInteger.ONE);
        param.setCode("CODE");
        param.setDescription("DESC");

        when(customizationParametersRepository.findById(BigInteger.ONE))
                .thenReturn(Optional.of(param));

        CustomParametersDetailsResponse response =
                service.getCustomParametersDetails(BigInteger.ONE);

        assertEquals("CODE", response.getCode());
    }

    @Test
    void shouldThrowWhenParameterNotFound() {
        when(customizationParametersRepository.findById(any(BigInteger.class)))
                .thenReturn(Optional.empty());

        assertThrows(CustomizationParametersNotFoundException.class,
                () -> service.getCustomParametersDetails(BigInteger.ONE));
    }

    @Test
    void shouldReturnCompanies_withDraft() {
        Page<CompaniesWithCustomParameterProjection> page = mock(Page.class);
        CompaniesWithCustomParameterProjection projection = mock(CompaniesWithCustomParameterProjection.class);

        when(projection.getCompanyId()).thenReturn(BigInteger.ONE);
        when(projection.getCompanyName()).thenReturn("C");
        when(projection.getCompanyCode()).thenReturn("C1");
        when(projection.getPublishedValue()).thenReturn("A");
        when(projection.getPreviewValue()).thenReturn("B");

        when(page.iterator()).thenReturn(List.of(projection).iterator()); 
        when(page.getTotalPages()).thenReturn(1);
        when(page.getTotalElements()).thenReturn(1L);

        when(customizationParametersRepository.getCompaniesWithCustomParameter(any(), any(), any()))
                .thenReturn(page);

        CompaniesWithCustomParameterResponse response =
                service.getCompaniesWithCustomParameter(BigInteger.ONE, null, 1, 10);

        assertTrue(response.getData().get(0).isHasDraft());
    }

    @Test
    void shouldReturnCompanies_withoutDraft() {
        CompaniesWithCustomParameterProjection projection = mock(CompaniesWithCustomParameterProjection.class);

        when(projection.getCompanyId()).thenReturn(BigInteger.ONE);
        when(projection.getCompanyName()).thenReturn("C");
        when(projection.getCompanyCode()).thenReturn("C1");
        when(projection.getPublishedValue()).thenReturn("A");
        when(projection.getPreviewValue()).thenReturn("A");

        Page<CompaniesWithCustomParameterProjection> page =
                new PageImpl<>(List.of(projection));

        when(customizationParametersRepository.getCompaniesWithCustomParameter(any(), any(), any()))
                .thenReturn(page);

        CompaniesWithCustomParameterResponse response =
                service.getCompaniesWithCustomParameter(BigInteger.ONE, null, 1, 10);

        assertFalse(response.getData().get(0).isHasDraft());
    }

    @Test
    void shouldDeleteCustomParameters() {
        service.deleteCustomParameters(BigInteger.ONE, BigInteger.TWO);

        verify(companyParametersPreviewRepository)
                .deleteByCompanyAndParamId(BigInteger.TWO, BigInteger.ONE);

        verify(companyParametersRepository)
                .deleteByCompanyAndParamId(BigInteger.TWO, BigInteger.ONE);
    }

    @Test
    void shouldPublishCustomParameters() {
        service.publishCustomParameters(BigInteger.ONE);

        verify(companyParametersRepository).updatePublishedFromDraft(BigInteger.ONE);
        verify(companyParametersPreviewRepository).deletePreview(BigInteger.ONE);
        verify(companyParametersPreviewRepository).insertPreview(BigInteger.ONE);
    }

    @Test
    void shouldReturnLookup() {
        when(customizationParametersRepository.getCustomParametersLookup(any()))
                .thenReturn(List.of(mock(CompanyLookupProjection.class)));

        List<CompanyLookupProjection> result =
                service.getCustomParametersLookup(BigInteger.ONE);

        assertEquals(1, result.size());
    }

    @Test
    void shouldSaveCustomParametersForCompany() {
        Company company = new Company();
        CustomizationParameters param = new CustomizationParameters();

        when(companyRepository.findById(any()))
                .thenReturn(Optional.of(company));
        when(customizationParametersRepository.findById(any(BigInteger.class)))
                .thenReturn(Optional.of(param));

        when(companyParametersRepository.existsByCompanyAndCustomizationParameters(any(), any()))
                .thenReturn(false);
        when(companyParametersPreviewRepository.existsByCompanyAndCustomizationParameters(any(), any()))
                .thenReturn(false);

        SaveCustomParametersForCompanyRequest request = new SaveCustomParametersForCompanyRequest();
        request.setCompanyId(BigInteger.ONE);
        request.setValue("VAL");

        service.saveCustomParametersForCompany(BigInteger.ONE, request);

        verify(companyParametersRepository).save(any());
        verify(companyParametersPreviewRepository).save(any());
    }

    @Test
    void shouldThrowWhenCustomParameterAlreadyExistsForCompany() {
        Company company = new Company();
        CustomizationParameters param = new CustomizationParameters();

        when(companyRepository.findById(any()))
                .thenReturn(Optional.of(company));
        when(customizationParametersRepository.findById(any(BigInteger.class)))
                .thenReturn(Optional.of(param));

        when(companyParametersRepository.existsByCompanyAndCustomizationParameters(any(), any()))
                .thenReturn(true);

        SaveCustomParametersForCompanyRequest request = new SaveCustomParametersForCompanyRequest();
        request.setCompanyId(BigInteger.ONE);

        assertThrows(CompanyParametersAlreadyExistException.class,
                () -> service.saveCustomParametersForCompany(BigInteger.ONE, request));
    }

    @Test
    void shouldDeleteDraft_whenSameValue() {
        Company company = new Company();
        CustomizationParameters param = new CustomizationParameters();

        CompanyParameters cp = new CompanyParameters();
        cp.setParameterValue("A");

        CompanyParametersPreview preview = new CompanyParametersPreview();
        preview.setParameterValue("B");

        when(companyRepository.findById(any()))
                .thenReturn(Optional.of(company));
        when(customizationParametersRepository.findById(any(BigInteger.class)))
                .thenReturn(Optional.of(param));
        when(companyParametersRepository.findByCompanyAndCustomizationParameters(any(), any()))
                .thenReturn(Optional.of(cp));
        when(companyParametersPreviewRepository.findByCompanyAndCustomizationParameters(any(), any()))
                .thenReturn(List.of(preview));

        service.putCustomParametersValue(BigInteger.ONE, BigInteger.ONE, "A");

        verify(companyParametersPreviewRepository).delete(preview);
    }

    @Test
    void shouldUpdateExistingDraft() {
        Company company = new Company();
        CustomizationParameters param = new CustomizationParameters();

        CompanyParameters cp = new CompanyParameters();
        cp.setParameterValue("A");

        CompanyParametersPreview preview = new CompanyParametersPreview();
        preview.setParameterValue("B");

        when(companyRepository.findById(any()))
                .thenReturn(Optional.of(company));
        when(customizationParametersRepository.findById(any(BigInteger.class)))
                .thenReturn(Optional.of(param));
        when(companyParametersRepository.findByCompanyAndCustomizationParameters(any(), any()))
                .thenReturn(Optional.of(cp));
        when(companyParametersPreviewRepository.findByCompanyAndCustomizationParameters(any(), any()))
                .thenReturn(List.of(preview));

        service.putCustomParametersValue(BigInteger.ONE, BigInteger.ONE, "C");

        verify(companyParametersPreviewRepository).save(preview);
    }

    @Test
    void shouldCreateNewDraft() {
        Company company = new Company();
        CustomizationParameters param = new CustomizationParameters();

        CompanyParameters cp = new CompanyParameters();
        cp.setParameterValue("A");

        when(companyRepository.findById(any()))
                .thenReturn(Optional.of(company));
        when(customizationParametersRepository.findById(any(BigInteger.class)))
                .thenReturn(Optional.of(param));
        when(companyParametersRepository.findByCompanyAndCustomizationParameters(any(), any()))
                .thenReturn(Optional.of(cp));
        when(companyParametersPreviewRepository.findByCompanyAndCustomizationParameters(any(), any()))
                .thenReturn(List.of());

        service.putCustomParametersValue(BigInteger.ONE, BigInteger.ONE, "C");

        verify(companyParametersPreviewRepository).save(any());
    }
}