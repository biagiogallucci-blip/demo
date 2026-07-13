package com.example.demo.controller;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.ExclusionRulesDto;
import com.example.demo.projection.CompanyLookupProjection;
import com.example.demo.request.CloneExclusionRuleRequest;
import com.example.demo.request.CompanyCreateRequest;
import com.example.demo.request.CreateExclusionDraftRequest;
import com.example.demo.request.CreateExclusionRuleRequest;
import com.example.demo.response.CloneExclusionRuleResponse;
import com.example.demo.response.CompaniesByExclusionRuleResponse;
import com.example.demo.response.CreateExclusionRuleResponse;
import com.example.demo.response.ExclusionRulesResponse;
import com.example.demo.service.ICompaniesService;
import com.example.demo.service.IExclusionRulesService;

@RestController
@RequestMapping("/api/v1/exclusion-rules")
public class ExclusionRulesController {
	
	@Autowired
	private IExclusionRulesService exclusionRulesService;
	
	@Autowired
	private ICompaniesService companiesService;

	@GetMapping
	public ExclusionRulesResponse getExclusionRules(@RequestParam(defaultValue = "1") Integer page,
			@RequestParam(defaultValue = "10") Integer limit, @RequestParam(required = false) String search,
			@RequestParam(defaultValue = "ALL", required = false) String status) {
		return exclusionRulesService.getExclusionRules(page, limit, search, status);
	}

	@PostMapping
	public CreateExclusionRuleResponse createExclusionRule(
			@RequestBody CreateExclusionRuleRequest createExclusionRuleRequest) {
		return exclusionRulesService.createExclusionRule(createExclusionRuleRequest);
	}

	@GetMapping("/{ruleId}")
	public ExclusionRulesDto getExclusionRuleById (@PathVariable("ruleId") BigInteger ruleId) {
		return exclusionRulesService.getExclusionRuleById(ruleId);
	}
	
	@GetMapping("/{ruleId}/companies")
	public CompaniesByExclusionRuleResponse getCompaniesByExclusionRuleId(@PathVariable("ruleId") BigInteger ruleId, 
			@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer limit, 
			@RequestParam(required = false) String search) {
		return exclusionRulesService.getCompaniesByExclusionRuleId(ruleId, page, limit, search);
	}
	
	@GetMapping("/lookup")
	public List<CompanyLookupProjection> getExclusionRuleLookup (@RequestParam("ruleId") BigInteger ruleId) {
		return exclusionRulesService.getExclusionRuleLookup(ruleId);
	}
	
	@PostMapping("/{ruleId}/companies")
	public void createExclusionRuleForCompany(@PathVariable("ruleId") BigInteger ruleId,
			@RequestBody CompanyCreateRequest companyCreateRequest) {
		exclusionRulesService.createExclusionRuleForCompany(ruleId, companyCreateRequest);
	}
	
	@PostMapping("/{ruleId}/clone")
	public CloneExclusionRuleResponse cloneExclusionRule (@PathVariable("ruleId") BigInteger ruleId,
			@RequestBody CloneExclusionRuleRequest cloneExclusionRuleRequest) {
		return exclusionRulesService.cloneExclusionRule(ruleId, cloneExclusionRuleRequest);
	}
	
	@PostMapping("{ruleId}/companies/{companyId}")
	    public void updateExclusionRuleForCompanyDraft (@PathVariable("companyId") BigInteger companyId, @PathVariable("ruleId") BigInteger ruleId,
	    		@RequestBody CreateExclusionDraftRequest createDraftRequest) {
	    	companiesService.updateDraft(companyId, ruleId, createDraftRequest);
	}
	
	@PostMapping("/{ruleId}/publish")
	public void publishExclusionRule (@PathVariable("ruleId") BigInteger ruleId) {
		exclusionRulesService.publishExclusionRule(ruleId);
	}
	
	@GetMapping("/export")
    public ResponseEntity<byte[]> exportAziende(@RequestParam(defaultValue = "1") Integer page,
			@RequestParam(defaultValue = "10") Integer limit, @RequestParam(required = false) String search,
			@RequestParam(required = false) String status) {
        
    	ExclusionRulesResponse response = exclusionRulesService.getExclusionRules(page, limit, search, status);

        if (response == null || response.getData() == null || response.getData().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        try {
            byte[] excelBytes = exclusionRulesService.generaExcelExclusionRules(response.getData());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", "esportazione_regole_esclusione.xlsx");
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentLength(excelBytes.length);

            return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);

        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}