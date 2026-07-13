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
import com.example.demo.response.Stats;
import com.example.demo.service.IParametersService;
import com.example.demo.utils.Constants;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

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
		
		int companiesCount = 0;

	    if (Boolean.TRUE.equals(createCustomParametersRequest.getApplyToAllCompanies())) {
	        companiesCount = companyParametersRepository.insertParametersForAllCompanies(
	                customParameter.getCode(),
	                createCustomParametersRequest.getValue());
	    }
		
		CreateCustomParametersResponse response = new CreateCustomParametersResponse();
		response.setId(customParameter.getId());
		response.setTitle(customParameter.getDescription());
		response.setCode(customParameter.getCode());
		response.setPlaceholder(Constants.PREFIX_VARIABLE.concat(customParameter.getCode()).concat(Constants.SUFFIX_VARIABLE));
		response.setStatus(Constants.PUBLISHED);
		response.setValue(createCustomParametersRequest.getValue());
		response.setCompaniesCount(companiesCount);
		
		return response;
	}

	@Override
	public CustomParametersDetailsResponse getCustomParametersDetails(BigInteger paramId) {
		CustomizationParameters result = customizationParametersRepository.findById(paramId).orElseThrow(() -> new CustomizationParametersNotFoundException(paramId));
		Integer pendingChange = companyParametersPreviewRepository.countByParameterCode(result.getCode());
		
		CustomParametersDetailsResponse response = new CustomParametersDetailsResponse();
        response.setId(result.getId());
		response.setTitle(result.getDescription());
		response.setCode(result.getCode());
		response.setPlaceholder(Constants.PREFIX_VARIABLE.concat(result.getCode()).concat(Constants.SUFFIX_VARIABLE));
		Stats stats = new Stats();
		stats.setPendingChanges(pendingChange);
		response.setStats(stats);
		
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
		companyParametersRepository.deleteByCompanyAndParamId(companyId, paramId);
	}

	@Override
	@Transactional
	public void publishCustomParameters(BigInteger paramId) {
		companyParametersRepository.updatePublishedFromDraft(paramId);
	    companyParametersPreviewRepository.deletePreview(paramId);
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
		cp.setCustomizationParameters(param);
		cp.setParameterValue(saveCustomParametersForCompanyRequest.getValue());

		companyParametersRepository.save(cp);

		CompanyParametersPreview cpp = new CompanyParametersPreview();
		cpp.setCompany(company);
		cpp.setCustomizationParameters(param);
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

		Optional<CompanyParametersPreview> previewOpt =
	            companyParametersPreviewRepository.findByCompanyAndCustomizationParameters(company, param);

		if (companyParameters.getParameterValue().equals(value)) {
			previewOpt.ifPresent(companyParametersPreviewRepository::delete);
	        return;
		}
		
		if (previewOpt.isPresent()) {
	        CompanyParametersPreview preview = previewOpt.get();
	        preview.setParameterValue(value);
	        companyParametersPreviewRepository.save(preview);
	    } else {
	        CompanyParametersPreview preview = new CompanyParametersPreview();
	        preview.setCompany(company);
	        preview.setCustomizationParameters(param);
	        preview.setParameterValue(value);
	        companyParametersPreviewRepository.save(preview);
	    }
	}

	@Override
	public byte[] generaExcelCustomParameters(List<CustomParametersDto> data) throws IOException {
		try (Workbook workbook = new XSSFWorkbook(); 
	             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
	            
	            Sheet sheet = workbook.createSheet("Parametri Custom");
	            sheet.setDisplayGridlines(true);

	            Font fontTitle = workbook.createFont();
	            fontTitle.setFontName("Segoe UI");
	            fontTitle.setFontHeightInPoints((short) 16);
	            fontTitle.setBold(true);

	            Font fontHeader = workbook.createFont();
	            fontHeader.setFontName("Segoe UI");
	            fontHeader.setFontHeightInPoints((short) 11);
	            fontHeader.setBold(true);
	            fontHeader.setColor(IndexedColors.WHITE.getIndex());

	            Font fontBody = workbook.createFont();
	            fontBody.setFontName("Segoe UI");
	            fontBody.setFontHeightInPoints((short) 11);

	            Font fontPublished = workbook.createFont();
	            fontPublished.setFontName("Segoe UI");
	            fontPublished.setFontHeightInPoints((short) 11);
	            fontPublished.setBold(true);
	            fontPublished.setColor(IndexedColors.GREEN.getIndex());

	            Font fontDraft = workbook.createFont();
	            fontDraft.setFontName("Segoe UI");
	            fontDraft.setFontHeightInPoints((short) 11);
	            fontDraft.setBold(true);
	            fontDraft.setColor(IndexedColors.ORANGE.getIndex());

	            CellStyle styleHeader = workbook.createCellStyle();
	            styleHeader.setFont(fontHeader);
	            styleHeader.setFillForegroundColor(IndexedColors.BLUE_GREY.getIndex());
	            styleHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	            styleHeader.setVerticalAlignment(VerticalAlignment.CENTER);
	            applyBorders(styleHeader);

	            CellStyle styleBodyLeft = workbook.createCellStyle();
	            styleBodyLeft.setFont(fontBody);
	            styleBodyLeft.setAlignment(HorizontalAlignment.LEFT);
	            styleBodyLeft.setVerticalAlignment(VerticalAlignment.CENTER);
	            applyBorders(styleBodyLeft);

	            CellStyle styleBodyCenter = workbook.createCellStyle();
	            styleBodyCenter.setFont(fontBody);
	            styleBodyCenter.setAlignment(HorizontalAlignment.CENTER);
	            styleBodyCenter.setVerticalAlignment(VerticalAlignment.CENTER);
	            applyBorders(styleBodyCenter);

	            Row titleRow = sheet.createRow(0);
	            titleRow.setHeightInPoints(35);
	            Cell titleCell = titleRow.createCell(0);
	            titleCell.setCellValue("Report Parametri Custom");
	            titleCell.setCellStyle(workbook.createCellStyle());
	            titleCell.getCellStyle().setFont(fontTitle);

	            String[] headers = {
	                "ID Parametro", 
	                "Titolo", 
	                "Codice", 
	                "Placeholder (Tag)",
	                "Aziende Associate", 
	                "Bozza Pendente", 
	                "Stato"
	            };
	            
	            Row headerRow = sheet.createRow(2);
	            headerRow.setHeightInPoints(26);
	            
	            for (int i = 0; i < headers.length; i++) {
	                Cell cell = headerRow.createCell(i);
	                cell.setCellValue(headers[i]);
	                
	                CellStyle currentHeaderStyle = workbook.createCellStyle();
	                currentHeaderStyle.cloneStyleFrom(styleHeader);
	                currentHeaderStyle.setAlignment((i == 0 || i >= 4) ? HorizontalAlignment.CENTER : HorizontalAlignment.LEFT);
	                cell.setCellStyle(currentHeaderStyle);
	            }

	            int rowIdx = 3;
	            if (data != null) {
	                for (CustomParametersDto param : data) {
	                    Row row = sheet.createRow(rowIdx++);
	                    row.setHeightInPoints(22);

	                    String id = param.getId() != null ? param.getId() : "";
	                    String title = param.getTitle() != null ? param.getTitle() : "";
	                    String code = param.getCode() != null ? param.getCode() : "";
	                    String placeholder = param.getPlaceholder() != null ? param.getPlaceholder() : "";
	                    int companiesCount = param.getCompaniesCount() != null ? param.getCompaniesCount() : 0;
	                    String bozzaPendente = Boolean.TRUE.equals(param.getHasDraft()) ? "Sì" : "No";
	                    String statusVal = param.getStatus() != null ? param.getStatus() : "";

	                    Cell c0 = row.createCell(0); c0.setCellValue(id); c0.setCellStyle(styleBodyCenter);
	                    Cell c1 = row.createCell(1); c1.setCellValue(title); c1.setCellStyle(styleBodyLeft);
	                    Cell c2 = row.createCell(2); c2.setCellValue(code); c2.setCellStyle(styleBodyLeft);
	                    Cell c3 = row.createCell(3); c3.setCellValue(placeholder); c3.setCellStyle(styleBodyLeft);
	                    
	                    Cell c4 = row.createCell(4); 
	                    c4.setCellValue(companiesCount); 
	                    c4.setCellStyle(styleBodyCenter);
	                    c4.getCellStyle().setDataFormat(workbook.createDataFormat().getFormat("#,##0"));
	                    
	                    Cell c5 = row.createCell(5); c5.setCellValue(bozzaPendente); c5.setCellStyle(styleBodyCenter);
	                    
	                    Cell c6 = row.createCell(6);
	                    c6.setCellValue(statusVal);
	                    CellStyle statusStyle = workbook.createCellStyle();
	                    statusStyle.cloneStyleFrom(styleBodyCenter);
	                    if ("PUBLISHED".equalsIgnoreCase(statusVal)) {
	                        statusStyle.setFont(fontPublished);
	                    } else if ("DRAFT".equalsIgnoreCase(statusVal)) {
	                        statusStyle.setFont(fontDraft);
	                    }
	                    c6.setCellStyle(statusStyle);
	                }
	            }

	            for (int i = 0; i < headers.length; i++) {
	                sheet.autoSizeColumn(i);
	                sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 1200);
	            }

	            workbook.write(out);
	            return out.toByteArray();
	        }
	}
	
	private static void applyBorders(CellStyle style) {
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setBottomBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setLeftBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setRightBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
    }
}