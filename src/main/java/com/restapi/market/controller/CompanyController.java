package com.restapi.market.controller;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.restapi.market.model.ChartObjectCustom;
import com.restapi.market.model.DailyData;

import com.restapi.market.service.CompanyService;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "http://localhost:51535")
@RestController
@RequestMapping("/data")
public class CompanyController {

	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

	@Autowired
	private CompanyService companyService;
	

	// Sorted values of Deviation Price or Volume
	@GetMapping("/sort")
	public Map<String, Double> getDeviation(@RequestParam("type") String type,
			@RequestParam("value") String value,
			@RequestParam(defaultValue = "2020-02-09") String boundaryDate) throws ParseException {
		return companyService.getDeviation(type,value, boundaryDate);
	}

	// companies and sector for grid
	@GetMapping("/grid/{startDate}/{endDate}")
	public List<DailyData> getGridData(@PathVariable("startDate") String startDate,
			@PathVariable("endDate") String endDate, @RequestParam(defaultValue = "") List<String> gotTickers,
			@RequestParam(defaultValue = "") List<String> gotSectors) throws ParseException {
		return companyService.getGridData(startDate, endDate, gotTickers, gotSectors);

	}

	// companies and sector for chart
	@GetMapping("/chart/{startDate}/{endDate}")
	public ChartObjectCustom getChart(@PathVariable("startDate") String startDate,
			@PathVariable("endDate") String endDate, @RequestParam(defaultValue = "") List<String> tickerList,
			@RequestParam(defaultValue = "") List<String> sectorList, 
			@RequestParam("type") String type,
			@RequestParam("group") String group,
			@RequestParam(defaultValue = "company")String option,
			@RequestParam(defaultValue = "2020-02-09") String boundaryDate) throws ParseException {
		return companyService.getChart(tickerList,sectorList,startDate,endDate,type,group,option,boundaryDate);
	}

}