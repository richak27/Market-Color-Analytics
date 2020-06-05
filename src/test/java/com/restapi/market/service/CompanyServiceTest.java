package com.restapi.market.service;

import static org.junit.jupiter.api.Assertions.*;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.restapi.market.model.Company;
import com.restapi.market.model.PriceAverage;
import com.restapi.market.model.Stock;
import com.restapi.market.model.VolumeAverage;
import com.restapi.market.repository.CompanyRepository;

class CompanyServiceTest {
	
	@InjectMocks //injects the mock dependencies
	CompanyService companyService;
	
	@Mock
	CompanyRepository companyRepository;
	
	Company company1, company2, company3, company4;

	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		List<Stock> stocks1 = new ArrayList<Stock>();
		stocks1.add(new Stock("2020-02-06", "pre", 100, 50, "01", "02"));
		stocks1.add(new Stock("2020-02-07", "pre", 120, 40, "01", "02"));
		stocks1.add(new Stock("2020-03-10", "post", 80, 70, "11", "03"));
		stocks1.add(new Stock("2020-03-11", "post", 100, 60, "11", "03"));
		
		List<Stock> stocks2 = new ArrayList<Stock>();
		stocks2.add(new Stock("2020-02-06", "pre", 80, 40, "01", "02"));
		stocks2.add(new Stock("2020-02-07", "pre", 130, 40, "01", "02"));
		stocks2.add(new Stock("2020-03-10", "post", 80, 70, "11", "03"));
		stocks2.add(new Stock("2020-03-11", "post", 100, 60, "11", "03"));
		
		List<Stock> stocks3 = new ArrayList<Stock>();
		stocks3.add(new Stock("2020-02-06", "pre", 120, 60, "01", "02"));
		stocks3.add(new Stock("2020-02-07", "pre", 100, 40, "01", "02"));
		stocks3.add(new Stock("2020-03-10", "post", 80, 90, "11", "03"));
		stocks3.add(new Stock("2020-03-11", "post", 100, 60, "11", "03"));
		
		List<Stock> stocks4 = new ArrayList<Stock>();
		stocks4.add(new Stock("2020-02-06", "pre", 110, 50, "01", "02"));
		stocks4.add(new Stock("2020-02-07", "pre", 180, 40, "01", "02"));
		stocks4.add(new Stock("2020-03-10", "post", 80, 70, "11", "03"));
		stocks4.add(new Stock("2020-03-11", "post", 70, 60, "11", "03"));
		
		company1 = new Company("id1", "DMart", "DMT", "Retail", stocks1);
		company2 = new Company("id2", "Future Grp", "BBZ", "Retail", stocks2);
		company3 = new Company("id3", "Blue Dart", "BDT", "Courier", stocks3);
		company4 = new Company("id4", "DHL", "DH", "Courier", stocks4);	
		
		List<Company> retail = new ArrayList<Company>();
		retail.add(company1);
		retail.add(company2);
		List<Company> courier = new ArrayList<Company>();
		courier.add(company3);
		courier.add(company4);
		
		
	}

	@Test
	void testCalAvgVolByCompany() {
		when(companyRepository.findByTicker(anyString())).thenReturn(company1);
		VolumeAverage volumeAverage = companyService.calAvgVolumeByCompany("DMT");
		assertEquals(45, volumeAverage.getPreCovidVolume());
		assertEquals(65, volumeAverage.getPostCovidVolume());
		assertEquals(20, volumeAverage.getDeviationVolume());
	}

	@Test
	void testCalAvgPriceByCompany() {
		
		when(companyRepository.findByTicker(anyString())).thenReturn(company2);
		PriceAverage priceAverage = companyService.calAvgPriceByCompany("BBZ");
		assertEquals(105, priceAverage.getPreCovidPrice());
		assertEquals(90, priceAverage.getPostCovidPrice());
		assertEquals(-15, priceAverage.getDeviationPrice());
	}

	@Test
	void testMonthlyCompany() throws ParseException {
		when(companyRepository.findByTicker("DMT")).thenReturn(company1);
		Map<String, Double> avgMap = companyService.MonthlyCompany("DMT", "2020-01-02", "2020-06-01", "price");
		assertEquals(110, avgMap.get("02"));
		assertEquals(90, avgMap.get("03"));
		avgMap = companyService.MonthlyCompany("DMT", "2020-01-02", "2020-06-01", "volume");
		assertEquals(45, avgMap.get("02"));
		assertEquals(65, avgMap.get("03"));
		avgMap = companyService.MonthlyCompany("DMT", "2020-01-02", "2020-06-01", "stonks");		
		assertEquals(null, avgMap);		
	}
	
	@Test
	void testWeeklyCompany() throws ParseException {
		when(companyRepository.findByTicker("BBZ")).thenReturn(company2);
		Map<String, Double> avgMap = companyService.WeeklyCompany("BBZ", "2020-01-02", "2020-06-01", "price");
		assertEquals(105, avgMap.get("01"));
		assertEquals(90, avgMap.get("11"));
		avgMap = companyService.WeeklyCompany("BBZ", "2020-01-02", "2020-06-01", "volume");
		assertEquals(40, avgMap.get("01"));
		assertEquals(65, avgMap.get("11"));
		avgMap = companyService.WeeklyCompany("BBZ", "2020-01-02", "2020-06-01", "stonks");		
		assertEquals(null, avgMap);		
	}

}
