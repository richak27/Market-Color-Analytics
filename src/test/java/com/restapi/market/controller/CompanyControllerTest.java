package com.restapi.market.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.restapi.market.service.CompanyService;

public class CompanyControllerTest{
	
	private MockMvc mockMvc;
	
	
	@InjectMocks
	CompanyController companyController;
	
	
	@Mock
	CompanyService companyService;
	
	
	@BeforeEach
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		
	}
	
	
	
	
	
	
}