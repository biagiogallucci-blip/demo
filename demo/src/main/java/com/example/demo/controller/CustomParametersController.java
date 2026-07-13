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

import com.example.demo.projection.CompanyLookupProjection;
import com.example.demo.request.CreateCustomParametersRequest;
import com.example.demo.request.CreateParameterDraftRequest;
import com.example.demo.request.SaveCustomParametersForCompanyRequest;
import com.example.demo.response.CompaniesWithCustomParameterResponse;
import com.example.demo.response.CreateCustomParametersResponse;
import com.example.demo.response.CustomParametersDetailsResponse;
import com.example.demo.response.CustomParametersResponse;
import com.example.demo.service.IParametersService;

@RestController
@RequestMapping("/api/v1/custom-parameters")
public class CustomParametersController {
	
	@Autowired
	private IParametersService parametersService;

	@GetMapping
	public CustomParametersResponse getCustomParameters(@RequestParam(defaultValue = "1") Integer page,
			@RequestParam(defaultValue = "10") Integer limit, @RequestParam(required = false) String search,
			@RequestParam(defaultValue = "ALL", required = false) String status) {
		return parametersService.getCustomParameters(page, limit, search, status);
	}
	
	@PostMapping
	public CreateCustomParametersResponse createCustomParameters(
			@RequestBody CreateCustomParametersRequest createCustomParametersRequest) {
		return parametersService.createCustomParameter(createCustomParametersRequest);
	}
	
	@GetMapping("/{paramId}")
	public CustomParametersDetailsResponse getCustomParametersDetails (@PathVariable("paramId") BigInteger paramId) {
		return parametersService.getCustomParametersDetails(paramId);
	}
	
	@GetMapping("/{paramId}/companies")
	public CompaniesWithCustomParameterResponse getCompaniesWithCustomParameter(
	        @PathVariable BigInteger paramId,
	        @RequestParam(required = false) String search,
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int limit) {

	    return parametersService.getCompaniesWithCustomParameter(paramId, search, page, limit);
	}
	
	@GetMapping("/{paramId}/companies/{companyId}")
	public void deleteCustomParameters(@PathVariable BigInteger paramId, @PathVariable BigInteger companyId) {
		parametersService.deleteCustomParameters(paramId, companyId);
	}
	
	@PostMapping("/{paramId}/publish")
	public void publishCustomParameters(@PathVariable BigInteger paramId) {
		parametersService.publishCustomParameters(paramId);
	}
	
	@GetMapping("/lookup")
	public List<CompanyLookupProjection> getCustomParametersLookup (@RequestParam("paramId") BigInteger paramId) {
		return parametersService.getCustomParametersLookup(paramId);
	}
	
	@PostMapping("/{paramId}/companies")
	public void saveCustomParametersForCompany(@PathVariable BigInteger paramId, 
			@RequestBody SaveCustomParametersForCompanyRequest saveCustomParametersForCompanyRequest) {
		parametersService.saveCustomParametersForCompany(paramId, saveCustomParametersForCompanyRequest);
	}
	
	@PostMapping("/{paramId}/companies/{companyId}")
	public void putCustomParametersValue(@PathVariable BigInteger paramId, 
			@PathVariable BigInteger companyId, @RequestBody CreateParameterDraftRequest createParameterDraftRequest) {
		parametersService.putCustomParametersValue(paramId, companyId, createParameterDraftRequest.getValue());
	}
	
	@GetMapping("/export")
    public ResponseEntity<byte[]> exportAziende(@RequestParam(defaultValue = "1") Integer page,
			@RequestParam(defaultValue = "10") Integer limit, @RequestParam(required = false) String search,
			@RequestParam(required = false) String status) {
        
    	CustomParametersResponse response = parametersService.getCustomParameters(page, limit, search, status);

        if (response == null || response.getData() == null || response.getData().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        try {
            byte[] excelBytes = parametersService.generaExcelCustomParameters(response.getData());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", "esportazione_parametri_custom.xlsx");
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentLength(excelBytes.length);

            return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);

        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}