package com.restapi.market.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

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
		stocks1.add(new Stock("2020-02-06", "pre", 100, 50,null,null));
		stocks1.add(new Stock("2020-02-07", "pre", 120, 40,null,null));
		stocks1.add(new Stock("2020-02-10", "post", 80, 70,null,null));
		stocks1.add(new Stock("2020-02-11", "post", 100, 60,null,null));
		
		List<Stock> stocks2 = new ArrayList<Stock>();
		stocks2.add(new Stock("2020-02-06", "pre", 80, 40,null,null));
		stocks2.add(new Stock("2020-02-07", "pre", 130, 40,null,null));
		stocks2.add(new Stock("2020-02-10", "post", 80, 70,null,null));
		stocks2.add(new Stock("2020-02-11", "post", 100, 60,null,null));
		
		List<Stock> stocks3 = new ArrayList<Stock>();
		stocks3.add(new Stock("2020-02-06", "pre", 120, 60,null,null));
		stocks3.add(new Stock("2020-02-07", "pre", 100, 40,null,null));
		stocks3.add(new Stock("2020-02-10", "post", 80, 90,null,null));
		stocks3.add(new Stock("2020-02-11", "post", 100, 60,null,null));
		
		List<Stock> stocks4 = new ArrayList<Stock>();
		stocks4.add(new Stock("2020-02-06", "pre", 110, 50,null,null));
		stocks4.add(new Stock("2020-02-07", "pre", 180, 40,null,null));
		stocks4.add(new Stock("2020-02-10", "post", 80, 70,null,null));
		stocks4.add(new Stock("2020-02-11", "post", 70, 60,null,null));
		
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
		//when(companyRepository.findByTicker(anyString())).thenReturn(company1);
		//VolumeAverage volumeAverage = companyService.calAvgVolumeByCompany("DMT");
		//assertEquals(45, volumeAverage.getPreCovidVolume());
		//assertEquals(65, volumeAverage.getPostCovidVolume());
		//assertEquals(20, volumeAverage.getDeviationVolume());
	}

	@Test
	void testCalAvgPriceByCompany() {
		
		//when(companyRepository.findByTicker(anyString())).thenReturn(company2);
		//PriceAverage priceAverage = companyService.calAvgPriceByCompany("BBZ");
		//assertEquals(105, priceAverage.getPreCovidPrice());
		//assertEquals(90, priceAverage.getPostCovidPrice());
		//assertEquals(-15, priceAverage.getDeviationPrice());
	}

	@Test
	void testCalAvgPriceBySector() {
				
		
		
	}

	@Test
	void testCalAvgVolumeBySector() {
		
		fail("Not yet implemented");
	}

	@Test
	void testGetSectorVolumeDeviation() {
		fail("Not yet implemented");
	}

	@Test
	void testGetSectorPriceDeviation() {
		fail("Not yet implemented");
	}

	@Test
	void testGetCompanyVolumeDeviation() {
		fail("Not yet implemented");
	}

	@Test
	void testGetCompanyPriceDeviation() {
		fail("Not yet implemented");
	}

}
