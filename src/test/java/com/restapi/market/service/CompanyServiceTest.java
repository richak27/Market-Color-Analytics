package com.restapi.market.service;

import static org.junit.jupiter.api.Assertions.*;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.restapi.market.model.AverageValues;
import com.restapi.market.model.Calculate;
import com.restapi.market.model.Company;
import com.restapi.market.model.Stock;
import com.restapi.market.repository.CompanyRepository;

class CompanyServiceTest {
	
	@InjectMocks //injects the mock dependencies
	CompanyService companyService;
	
	@Mock
	CompanyRepository companyRepository;
	Company company1, company2, company3, company4,company5,company6,company7,company8;
	List<Company> tech_sector =new ArrayList<Company>();
	List<Company> retail = new ArrayList<Company>();
	List<Stock> stocks1 = new ArrayList<Stock>();

	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		stocks1.add(new Stock("2020-02-06",  100, 50, 01, "02"));
		stocks1.add(new Stock("2020-02-07",  120, 40, 01, "02"));
		stocks1.add(new Stock("2020-03-10",  80, 70, 11, "03"));
		stocks1.add(new Stock("2020-03-11",  100, 60, 11, "03"));
		
		List<Stock> stocks2 = new ArrayList<Stock>();
		stocks2.add(new Stock("2020-02-06", 80, 40, 01, "02"));
		stocks2.add(new Stock("2020-02-07",  130, 40, 01, "02"));
		stocks2.add(new Stock("2020-03-10",  80, 70, 11, "03"));
		stocks2.add(new Stock("2020-03-11",  100, 60, 11, "03"));
		
		List<Stock> stocks3 = new ArrayList<Stock>();
		stocks3.add(new Stock("2020-02-06", 120, 60, 01, "02"));
		stocks3.add(new Stock("2020-02-07",  100, 40, 01, "02"));
		stocks3.add(new Stock("2020-03-10", 80, 90, 11, "03"));
		stocks3.add(new Stock("2020-03-11",  100, 60, 11, "03"));
		
		List<Stock> stocks4 = new ArrayList<Stock>();
		stocks4.add(new Stock("2020-02-06", 110, 50, 01, "02"));
		stocks4.add(new Stock("2020-02-07", 180, 40, 01, "02"));
		stocks4.add(new Stock("2020-03-10", 80, 70, 11, "03"));
		stocks4.add(new Stock("2020-03-11", 70, 60, 11, "03"));
		
		company1 = new Company("id1", "DMart", "DMT", "Retail", stocks1);
		company2 = new Company("id2", "Future Grp", "BBZ", "Retail", stocks2);
		company3 = new Company("id3", "Blue Dart", "BDT", "Courier", stocks3);
		company4 = new Company("id4", "DHL", "DH", "Courier", stocks4);	

		company5 = new Company("id5", "APPLE INC", "AAPL", "Technology", stocks1);
		company6 = new Company("id6", "ADVANCED MICRO DEVICES", "AMD", "Technology", stocks2);
		company7 = new Company("id7", "GROUPON INC", "GRPN", "Technology", stocks3);
		company8 = new Company("id8", "GRUBHUB INC", "GRUB", "Technology", stocks4);	
		
		tech_sector.add(company5);
		tech_sector.add(company6);
		tech_sector.add(company7);
		tech_sector.add(company8);
		
		retail.add(company1);
		retail.add(company2);
		List<Company> courier = new ArrayList<Company>();
		courier.add(company3);
		courier.add(company4);
		
		
	}

	@Test  
	void testcalAvgVolumeByCompany() throws ParseException {
		when(companyRepository.findByTicker(anyString())).thenReturn(company1);
		AverageValues volumeAverage = companyService.calAvgVolumeByCompany("DMT","2020-02-09");
		assertEquals(45, volumeAverage.getPreCovidValue());
		assertEquals(65, volumeAverage.getPostCovidValue());
		assertEquals(20, volumeAverage.getDeviation());
	}

	@Test
	void testcalAvgPriceByCompany() throws ParseException {
		
		when(companyRepository.findByTicker(anyString())).thenReturn(company2);
		AverageValues priceAverage = companyService.calAvgPriceByCompany("BBZ","2020-02-09");
		assertEquals(105, priceAverage.getPreCovidValue());
		assertEquals(90, priceAverage.getPostCovidValue());
		assertEquals(-15, priceAverage.getDeviation());
	}
	
	
	@Test
	void testcalAvgVolumeBySector() throws ParseException {
		
		when(companyRepository.findBySector(anyString())).thenReturn(tech_sector);
		when(companyRepository.findByTicker(company5.getTicker())).thenReturn(company5);
		when(companyRepository.findByTicker(company6.getTicker())).thenReturn(company6);
		when(companyRepository.findByTicker(company7.getTicker())).thenReturn(company7);
		when(companyRepository.findByTicker(company8.getTicker())).thenReturn(company8);

		AverageValues volumeAverage = companyService.calAvgVolumeBySector("Technology","2020-02-09");
		assertEquals(45, volumeAverage.getPreCovidValue());
		assertEquals(67.5, volumeAverage.getPostCovidValue());
		assertEquals(22.5, volumeAverage.getDeviation());
	}
	

	@Test
	void testcalAvgPriceBySector() throws ParseException {
		
		when(companyRepository.findBySector(anyString())).thenReturn(tech_sector);
		when(companyRepository.findByTicker(company5.getTicker())).thenReturn(company5);
		when(companyRepository.findByTicker(company6.getTicker())).thenReturn(company6);
		when(companyRepository.findByTicker(company7.getTicker())).thenReturn(company7);
		when(companyRepository.findByTicker(company8.getTicker())).thenReturn(company8);
		AverageValues priceAverage = companyService.calAvgPriceBySector("Technology","2020-02-09");
		assertEquals(117.5, priceAverage.getPreCovidValue());
		assertEquals(86.25, priceAverage.getPostCovidValue());
		assertEquals(-31.25, priceAverage.getDeviation());
	}
	
    @Test
    void testCompanyAverage() throws ParseException{
		when(companyRepository.findByTicker(anyString())).thenReturn(company3);
		AverageValues volumeAverage = companyService.CompanyAverage("BDT","volume","2020-02-09");
		assertEquals(50, volumeAverage.getPreCovidValue());
		assertEquals(75, volumeAverage.getPostCovidValue());
		assertEquals(25, volumeAverage.getDeviation());
    	
    }
	
	@Test
	void testSectorAverage() throws ParseException {
		
		when(companyRepository.findBySector(anyString())).thenReturn(retail);
		when(companyRepository.findByTicker(company1.getTicker())).thenReturn(company1);
		when(companyRepository.findByTicker(company2.getTicker())).thenReturn(company2);
		AverageValues priceAverage = companyService.SectorAverage("Retail","price","2020-02-09");
		assertEquals(107.5, priceAverage.getPreCovidValue());
		assertEquals(90, priceAverage.getPostCovidValue());
		assertEquals(-17.5, priceAverage.getDeviation());
		
	}
	
	@Test
	void testgetSectorVolumeDeviation() throws ParseException {
		
		when(companyRepository.findBySector(anyString())).thenReturn(retail);
		when(companyRepository.findByTicker(company1.getTicker())).thenReturn(company1);
		when(companyRepository.findByTicker(company2.getTicker())).thenReturn(company2);
		AverageValues priceAverage = companyService.SectorAverage("Retail","price","2020-02-09");
		assertEquals(107.5, priceAverage.getPreCovidValue());
		assertEquals(90, priceAverage.getPostCovidValue());
		assertEquals(-17.5, priceAverage.getDeviation());
		
	}
	
	@Test
	void testaveragestock() throws ParseException {

		Calculate cal = companyService.averagestock(stocks1);
		assertEquals(55,cal.getVolume());
		assertEquals(100,cal.getPrice());
	}
	
	@Test
	void testgetDataByRangeCompany() throws ParseException{
		 Calculate cal=new Calculate();
		 when(companyRepository.findByTicker(anyString())).thenReturn(company4);
		 cal = companyService.getDataByRangeCompany("DH","2020-02-06","2020-03-11");
		assertEquals(55,cal.getVolume());
		assertEquals(110,cal.getPrice());

	}
	
	@Test
	void testgetDataByRangeSector() throws ParseException{
		 Calculate cal=new Calculate();
		 when(companyRepository.findBySector(anyString())).thenReturn(tech_sector);
		 cal = companyService.getDataByRangeSector("Technology","2020-02-06","2020-03-11");
		assertEquals(56.25,cal.getVolume());
		assertEquals(101.875,cal.getPrice());

	}
	
	
	@Test
	void testWeeklyCompany() throws ParseException {
		when(companyRepository.findByTicker("BBZ")).thenReturn(company2);
		Map<Integer, Double> avgMap = companyService.WeeklyCompany("BBZ", "2020-01-02", "2020-06-01", "price");
		assertEquals(105, avgMap.get("01"));
		assertEquals(90, avgMap.get("11"));
		avgMap = companyService.WeeklyCompany("BBZ", "2020-01-02", "2020-06-01", "volume");
		assertEquals(40, avgMap.get("01"));
		assertEquals(65, avgMap.get("11"));
		avgMap = companyService.WeeklyCompany("BABA", "2020-03-05", "2020-06-06", "price");		
		assertEquals(null, avgMap);		
	}
	

}
