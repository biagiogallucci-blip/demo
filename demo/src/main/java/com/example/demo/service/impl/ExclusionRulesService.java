package com.example.demo.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
import com.example.demo.response.Stats;
import com.example.demo.service.IExclusionRulesService;
import com.example.demo.utils.ActiveFlag;
import com.example.demo.utils.Constants;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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

	@Override
	public ExclusionRulesResponse getExclusionRules(Integer page, Integer limit, String search, String status) {
		Page<ExclusionRulesProjection> exclusionRulePage = categoriesRepository.getExclusionRules(search, status, PageRequest.of(page-1, limit));
		
		List<ExclusionRulesDto> exclusionRules = new ArrayList<>();
		
		for(ExclusionRulesProjection projection : exclusionRulePage.getContent()) {
			ExclusionRulesDto exclusionRule = new ExclusionRulesDto();
			exclusionRule.setId(projection.getId());
			exclusionRule.setName(projection.getName());
			exclusionRule.setTag(projection.getCode());
			exclusionRule.setCompaniesCount(projection.getCompanyCount().intValue());
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
		
		long companiesCount = 0;

		if (Boolean.TRUE.equals(createExclusionRuleRequest.getApplyToAllCompanies())) {
		    companyCategoriesRepository.insertCategoriesForAllCompanies(category.getCode());
		    companyCategoriesPreviewRepository.insertCategoriesPreviewForAllCompanies(category.getCode());
		    companiesCount = companyRepository.count();
		}

		CreateExclusionRuleResponse response = new CreateExclusionRuleResponse();
		response.setId(category.getId());
		response.setName(category.getName());
		response.setTag(category.getCode());
		response.setStatus(createExclusionRuleRequest.getStatus());
		response.setCompaniesCount(companiesCount);
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
		Stats stats = new Stats();
		stats.setPendingChanges(projection.getPendingCount().intValue());
		response.setStats(stats);
	    return response;
	}

	@Override
	public CompaniesByExclusionRuleResponse getCompaniesByExclusionRuleId(BigInteger ruleId, Integer page, Integer limit, String search) {
		Page<CompaniesByExclusionRuleProjection> result = companyCategoriesRepository.getCompaniesByExclusionRuleId(ruleId, search, PageRequest.of(page-1, limit));
		
		List<CompaniesByExclusionRuleDto> companiesByExclusionRule = new ArrayList<>();
		
		for(CompaniesByExclusionRuleProjection projection : result.getContent()) {
			CompaniesByExclusionRuleDto dto = new CompaniesByExclusionRuleDto();
			
			String apiStatus = Constants.PUBLISHED;

		    boolean hasDraft = Constants.DRAFT.equals(projection.getStatus());

		    if (hasDraft) {
		        apiStatus = Constants.PENDING_PUBLICATION;
		        DraftExclusionRules draft = new DraftExclusionRules();
		        draft.setStatus(Constants.DRAFT);
		        draft.setIsEnabled(Boolean.FALSE);
		        dto.setDraft(draft);
		    }

		    dto.setCompanyId(projection.getCompanyId());
		    dto.setCompanyName(projection.getCompanyName());
		    dto.setCompanyCode(projection.getCompanyCode());

		    dto.setStatus(apiStatus);
		    dto.setHasDraft(hasDraft);
		    dto.setIsEnabled(Boolean.TRUE);

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
		companyCategoriesRepository.save(entity);

		CompanyCategoriesPreview entityPreview = new CompanyCategoriesPreview();
		entityPreview.setCompany(company);
		entityPreview.setCategory(projection.getCategory());
		companyCategoriesPreviewRepository.save(entityPreview);
	}

	@Override
	@Transactional
	public CloneExclusionRuleResponse cloneExclusionRule(BigInteger ruleId, CloneExclusionRuleRequest cloneExclusionRuleRequest) {
		categoriesRepository.findById(cloneExclusionRuleRequest.getNewTag()).ifPresent(category -> {
			throw new CategoriesAlreadyExistException(cloneExclusionRuleRequest.getNewTag());
		});
		Categories categories = new Categories();
		categories.setName(cloneExclusionRuleRequest.getNewName());
		categories.setCode(cloneExclusionRuleRequest.getNewTag());
		categories.setActive(ActiveFlag.Y);
		categories.setCreation(Timestamp.from(Instant.now()));
		categories.setId(categoriesRepository.getNextId());
		
		categoriesRepository.saveAndFlush(categories);
		
		Categories source = categoriesRepository.findByBusinessId(ruleId)
				.orElseThrow(() -> new CategoriesNotFoundException(ruleId));
		
		companyCategoriesRepository.cloneCompanyCategories(
				source.getCode(),
		        categories.getCode());

		companyCategoriesPreviewRepository.cloneCompanyCategoriesPreview(
				source.getCode(),
		        categories.getCode());
		
		CloneExclusionRuleResponse response = new CloneExclusionRuleResponse();
		response.setId(categories.getId());
		response.setName(categories.getName());
		
		return response;
	}

	@Override
	@Transactional
	public void publishExclusionRule(BigInteger ruleId) {
		List<BigInteger> ids = companyCategoriesPreviewRepository.findExclusionRuleToPublish(ruleId);
		companyCategoriesRepository.deleteAllById(ids);
	}

	@Override
	public byte[] generaExcelExclusionRules(List<ExclusionRulesDto> data) throws IOException {
		try (Workbook workbook = new XSSFWorkbook(); 
	             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
	            
	            Sheet sheet = workbook.createSheet("Regole Esclusione");
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
	            titleCell.setCellValue("Report Regole di Esclusione");
	            titleCell.setCellStyle(workbook.createCellStyle());
	            titleCell.getCellStyle().setFont(fontTitle);

	            String[] headers = {
	                "ID Regola", 
	                "Nome Regola", 
	                "Tag Regola", 
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
	                currentHeaderStyle.setAlignment((i == 0 || i >= 3) ? HorizontalAlignment.CENTER : HorizontalAlignment.LEFT);
	                cell.setCellStyle(currentHeaderStyle);
	            }

	            int rowIdx = 3;
	            for (ExclusionRulesDto regola : data) {
	                Row row = sheet.createRow(rowIdx++);
	                row.setHeightInPoints(22);

	                String id = regola.getId() != null ? regola.getId().toString() : "";
	                String name = regola.getName() != null ? regola.getName() : "";
	                String tag = regola.getTag() != null ? regola.getTag() : "";
	                int companiesCount = regola.getCompaniesCount() != null ? regola.getCompaniesCount() : 0;
	                String bozzaPendente = Boolean.TRUE.equals(regola.getHasDraft()) ? "Sì" : "No";
	                String statusVal = regola.getStatus() != null ? regola.getStatus() : "";

	                Cell c0 = row.createCell(0); c0.setCellValue(id); c0.setCellStyle(styleBodyCenter);
	                Cell c1 = row.createCell(1); c1.setCellValue(name); c1.setCellStyle(styleBodyLeft);
	                Cell c2 = row.createCell(2); c2.setCellValue(tag); c2.setCellStyle(styleBodyLeft);
	                
	                Cell c3 = row.createCell(3); 
	                c3.setCellValue(companiesCount); 
	                c3.setCellStyle(styleBodyCenter);
	                c3.getCellStyle().setDataFormat(workbook.createDataFormat().getFormat("#,##0"));
	                
	                Cell c4 = row.createCell(4); c4.setCellValue(bozzaPendente); c4.setCellStyle(styleBodyCenter);
	                
	                Cell c5 = row.createCell(5);
	                c5.setCellValue(statusVal);
	                CellStyle statusStyle = workbook.createCellStyle();
	                statusStyle.cloneStyleFrom(styleBodyCenter);
	                if ("PUBLISHED".equalsIgnoreCase(statusVal)) {
	                    statusStyle.setFont(fontPublished);
	                } else if ("DRAFT".equalsIgnoreCase(statusVal)) {
	                    statusStyle.setFont(fontDraft);
	                }
	                c5.setCellStyle(statusStyle);
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