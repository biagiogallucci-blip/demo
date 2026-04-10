package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.ParametersDefinitionDto;
import com.example.demo.service.IParametersService;

@RestController
@RequestMapping("/api/v1/parameter-definitions")
public class ParametersDefinitionsController {
	
	@Autowired
	IParametersService parametersService;
	
	@GetMapping
    public List<ParametersDefinitionDto> getParameterDefinitions() {
        return parametersService.getParameterDefinitions();
    }
}