package com.restapi.market.controller;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.restapi.market.model.DailyData;
import com.restapi.market.service.CompanyService;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


import com.restapi.market.model.ChartObject;
import com.restapi.market.model.ChartObjectCustom;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CompanyControllerTest{
	
	private MockMvc mockMvc;
	
	
	@InjectMocks
	CompanyController companyController;
		
	@Mock
	CompanyService companyService;
	
	ChartObjectCustom obj=new ChartObjectCustom();
	ChartObjectCustom obj1=new ChartObjectCustom();

	public List<String> label=new ArrayList<>();
	public List<Double> data=new ArrayList<>();
	public List<ChartObject> chartlist= new ArrayList<>();
	public ChartObject chartobj=new ChartObject();

	

	@BeforeEach
	void setUp() throws Exception {
		
		MockitoAnnotations.initMocks(this);

		mockMvc=MockMvcBuilders.standaloneSetup(companyController).build();
	}
	
	@Test
	public void testGetGridData() throws Exception {
		List<DailyData> datalist= new ArrayList<>();
		DailyData dailydata = new DailyData();
		dailydata.setCompanyName("APPLE");
		dailydata.setDate("2020-05-05");
		dailydata.setPrice("56");
		dailydata.setSector("Technology");
		dailydata.setTicker("APPl");
		dailydata.setVolume("42");
		datalist.add(dailydata);
		
		when(companyController.getGridData(anyString(),anyString(),Mockito.anyListOf(String.class),Mockito.anyListOf(String.class))).thenReturn(datalist);
		
		String URI = "/data/grid/2020-02-02/2020-03-03";
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(URI).accept(
				MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		String expectedJson = this.mapToJson(datalist);
		String outputInJson = result.getResponse().getContentAsString();

		assertEquals(outputInJson,expectedJson);
	
	}
	
	@Test
	public void testGetChart() throws Exception {
		
		label.add("Jan");
		label.add("Feb");
		label.add("Mar");
		
		data.add((double) 56);
		data.add((double) 45);
		data.add((double) 54);

		chartobj.setLabel("Energy");
		chartobj.setBorderColor("pink");
		chartobj.setBackgroundColor("white");
		chartobj.setFill();
		chartobj.setData(data);
		
		chartlist.add(chartobj);
		
		obj.setLabels(label);
		obj.setDatasets(chartlist);
		
		when(companyController.getChart(anyString(),anyString(),Mockito.anyListOf(String.class),Mockito.anyListOf(String.class),anyString(),anyString(),anyString(),anyString())).thenReturn(obj);

		ChartObjectCustom obj=new ChartObjectCustom();
		ChartObjectCustom obj1=new ChartObjectCustom();

		List<String> label=new ArrayList<>();
		List<Double> data=new ArrayList<>();
		List<ChartObject> chartlist= new ArrayList<>();
		ChartObject chartobj=new ChartObject();

		label.add("Jan");
		label.add("Feb");
		label.add("Mar");
		
		data.add((double) 56);
		data.add((double) 45);
		data.add((double) 54);

		chartobj.setLabel("Energy");
		chartobj.setBorderColor("pink");
		chartobj.setBackgroundColor("white");
		chartobj.setFill();
		chartobj.setData(data);
		
		chartlist.add(chartobj);
		
		obj.setLabels(label);
		obj.setDatasets(chartlist);
				
		String URI = "/data/chart/2020-02-02/2020-05-05?boundaryDate=2020-02-09&group=monthly&option=company&rank=volume";
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(URI)
				.accept(MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		
		String outputInJson = result.getResponse().getContentAsString();


		assertEquals("",outputInJson);
	
	}
	

	@Test
	public void testgetDeviation() throws Exception {
		Map<String,Double> SectorDeviation= new HashMap<>();
		SectorDeviation.put("A", new Double(100)); 
		SectorDeviation.put("B", new Double(200)); 
		SectorDeviation.put("C", new Double(300)); 
		SectorDeviation.put("D", new Double(400)); 
		when(companyController.getDeviation(anyString(),anyString(),anyString())).thenReturn(SectorDeviation);
		
		String URI1 = "/data/sort?value=sector&boundaryDate=2020-02-09&type=volume";
		RequestBuilder requestBuilder1 = MockMvcRequestBuilders.get(URI1).accept(
				MediaType.APPLICATION_JSON);

		MvcResult result1 = mockMvc.perform(requestBuilder1).andReturn();
		String expectedJson1 = this.mapToJsonMap(SectorDeviation);
		String outputInJson1 = result1.getResponse().getContentAsString();


		assertEquals(outputInJson1,expectedJson1);
		
		Map<String,Double> companyDeviation= new HashMap<>();
		companyDeviation.put("A", new Double(100)); 
		companyDeviation.put("B", new Double(200)); 
		companyDeviation.put("C", new Double(300)); 
		companyDeviation.put("D", new Double(400)); 
		when(companyController.getDeviation(anyString(),anyString(),anyString())).thenReturn(companyDeviation);
		
		String URI2 = "/data/sort?value=company&boundaryDate=2020-02-09&type=volume";
		RequestBuilder requestBuilder2 = MockMvcRequestBuilders.get(URI2).accept(
				MediaType.APPLICATION_JSON);

		MvcResult result2 = mockMvc.perform(requestBuilder2).andReturn();
		String expectedJson2 = this.mapToJsonMap(companyDeviation);
		String outputInJson2 = result2.getResponse().getContentAsString();

		assertEquals(outputInJson2,expectedJson2);

	
	}
	

	
	private String mapToJsonMap(Map<String,Double> object) throws JsonProcessingException {
			ObjectMapper objectMapper = new ObjectMapper();
			return objectMapper.writeValueAsString(object);
		}
	
	private String mapToJson(List<DailyData> object) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.writeValueAsString(object);
	}
	
	}
	