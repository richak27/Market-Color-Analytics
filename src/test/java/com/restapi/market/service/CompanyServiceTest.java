package com.restapi.market.service;

import static org.junit.jupiter.api.Assertions.*;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.restapi.market.model.AverageValues;
import com.restapi.market.model.Calculate;
import com.restapi.market.model.ChartObjectCustom;
import com.restapi.market.model.Company;
import com.restapi.market.model.DailyData;
import com.restapi.market.model.Stock;
import com.restapi.market.repository.CompanyRepository;

class CompanyServiceTest {
	
	@InjectMocks //injects the mock dependencies
	CompanyService companyService;
	
	@Mock
	CompanyRepository companyRepository;
	
	Company company1, company2, company3, company4, company5, company6, company7, company8;
	
	List<Company> tech_sector =new ArrayList<Company>();
	List<Company> retail = new ArrayList<Company>();
	List<Company> courier = new ArrayList<Company>();

	List<String> sectors=new ArrayList<String>();
	List<String> tickers=new ArrayList<String>();
	
		@BeforeEach
		void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		List<Stock> stocks1 = new ArrayList<Stock>();
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
		
		courier.add(company3);
		courier.add(company4);
		
		sectors.add("Technology");
		sectors.add("Retail");
		sectors.add("Courier");

		tickers.add("DMT");
		tickers.add("DH");
		tickers.add("BDT");

	}

		
		//1. Average Volume for a company

		@Test  
		void testcalAvgVolumeByCompany() throws ParseException {
			
			when(companyRepository.findByTicker(anyString())).thenReturn(company1);
			
			AverageValues volumeAverage = companyService.calAvgVolumeByCompany("DMT","2020-02-09");
			
			assertEquals(45, volumeAverage.getPreCovidValue());
			assertEquals(65, volumeAverage.getPostCovidValue());
			assertEquals(20, volumeAverage.getDeviation());
		}
		
		
		//2. Average Price for a company
		@Test
		void testcalAvgPriceByCompany() throws ParseException {
			
			when(companyRepository.findByTicker(anyString())).thenReturn(company2);
			
			AverageValues priceAverage = companyService.calAvgPriceByCompany("BBZ","2020-02-09");
			
			assertEquals(105, priceAverage.getPreCovidValue());
			assertEquals(90, priceAverage.getPostCovidValue());
			assertEquals(-15, priceAverage.getDeviation());
		}
				
		//3. Average Volume for a Sector
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
		
		
		//4. Average Price for a Sector
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
			
		
		// 5. Calculate Average Value of the mentioned parameter (price/volume)  for a Company
		@Test
	    void testCompanyAverage() throws ParseException{
			
	    	when(companyRepository.findByTicker(anyString())).thenReturn(company3);
			
	    	AverageValues volumeAverage = companyService.companyAverage("BDT","volume","2020-02-09");
			
			assertEquals(50, volumeAverage.getPreCovidValue());
			assertEquals(75, volumeAverage.getPostCovidValue());
			assertEquals(25, volumeAverage.getDeviation());
			
			AverageValues priceAverage = companyService.companyAverage("BDT","price","2020-02-09");

			assertEquals(110, priceAverage.getPreCovidValue());
			assertEquals(90, priceAverage.getPostCovidValue());
			assertEquals(-20, priceAverage.getDeviation());
			
			
			
	     }
		
		
		// 6. Calculate Average Value of the mentioned parameter (price/volume)  for a Sector
		@Test
		void testSectorAverage() throws ParseException {
			
			when(companyRepository.findBySector(anyString())).thenReturn(retail);
			when(companyRepository.findByTicker(company1.getTicker())).thenReturn(company1);
			when(companyRepository.findByTicker(company2.getTicker())).thenReturn(company2);
			
			AverageValues priceAverage = companyService.sectorAverage("Retail","price","2020-02-09");
			assertEquals(107.5, priceAverage.getPreCovidValue());
			assertEquals(90, priceAverage.getPostCovidValue());
			assertEquals(-17.5, priceAverage.getDeviation());
			

			AverageValues volumeAverage = companyService.sectorAverage("Retail","volume","2020-02-09");
			assertEquals(42.5, volumeAverage.getPreCovidValue());
			assertEquals(65, volumeAverage.getPostCovidValue());
			assertEquals(22.5, volumeAverage.getDeviation());
		}
		
		
		@Test
		void testgridCompany() throws ParseException {
			
			when(companyRepository.findByTicker(anyString())).thenReturn(company1);
			
			List<DailyData> objnew=new ArrayList<>();

			DailyData dataobj=new DailyData();
			dataobj.setTicker("DMT");
			dataobj.setCompanyName("DMart");
			dataobj.setDate("2020-02-06");
			dataobj.setPrice("100");
			dataobj.setSector("Retail");
			dataobj.setVolume("50");
					
			objnew=companyService.gridCompany("DMT", "2020-02-06","2020-02-06");
			assertEquals(dataobj.getCompanyName(), objnew.get(0).getCompanyName());
			assertEquals(dataobj.getDate(),objnew.get(0).getDate());
			assertEquals(dataobj.getVolume(),objnew.get(0).getVolume());
			assertEquals(dataobj.getPrice(),objnew.get(0).getPrice());
		
		}

		

		@Test
		void testgetdataCompany() throws ParseException {
		
		when(companyRepository.findByTicker("DMT")).thenReturn(company1);
		when(companyRepository.findByTicker("BBZ")).thenReturn(company2);
		when(companyRepository.findBySector("Retail")).thenReturn(retail);
		
		List<String> tickerlist = new ArrayList<String>();
		tickerlist.add("DMT");
		tickerlist.add("BBZ");
		
		try{
	        ChartObjectCustom Object =companyService.getDataCompany(tickerlist,"2020-02-19","2020-04-12","price","daily","2020-02-09");
	        assertNotNull(Object);//check if the object is != null
	        
	        assertEquals( true, Object instanceof ChartObjectCustom);
	    }catch(Exception e){
       
	    	fail("got Exception");
	     }
		
		
		try{
	        ChartObjectCustom Object =companyService.getDataCompany(tickerlist,"2020-02-19","2020-04-12","volume","weekly","2020-02-09");
	        assertNotNull(Object);//check if the object is != null
	        
	        assertEquals( true, Object instanceof ChartObjectCustom);
	    }catch(Exception e){
       
	    	fail("got Exception");
	     }
		
		
		try{
	        ChartObjectCustom Object =companyService.getDataCompany(tickerlist,"2020-02-19","2020-04-12","volume","monthly","2020-02-09");
	        assertNotNull(Object);//check if the object is != null
	        
	        assertEquals( true, Object instanceof ChartObjectCustom);
	    }catch(Exception e){
       
	    	fail("got Exception");
	     }
		
	}
		
		@Test
		void testgetDataSector() {
			when(companyRepository.findByTicker("DMT")).thenReturn(company1);
			when(companyRepository.findByTicker("BBZ")).thenReturn(company2);
			when(companyRepository.findBySector("Retail")).thenReturn(retail);
			
			List<String> sectorlist = new ArrayList<String>();
			sectorlist.add("Retail");
			
			try{
		        ChartObjectCustom Object =companyService.getDataSector(sectorlist,"2020-02-19","2020-04-12","price","daily","2020-02-09");
		        assertNotNull(Object);//check if the object is != null
		        
		        assertEquals( true, Object instanceof ChartObjectCustom);
		    }catch(Exception e){
	       
		    	fail("got Exception");
		     }
			
			try{
		        ChartObjectCustom Object =companyService.getDataSector(sectorlist,"2020-02-19","2020-04-12","volume","daily","2020-02-09");
		        assertNotNull(Object);//check if the object is != null
		        
		        assertEquals( true, Object instanceof ChartObjectCustom);
		    }catch(Exception e){
	       
		    	fail("got Exception");
		     }
			
			try{
		        ChartObjectCustom Object =companyService.getDataSector(sectorlist,"2020-02-19","2020-04-12","price","weekly","2020-02-09");
		        assertNotNull(Object);//check if the object is != null
		        
		        assertEquals( true, Object instanceof ChartObjectCustom);
		    }catch(Exception e){
	       
		    	fail("got Exception");
		     }
			
			try{
		        ChartObjectCustom Object =companyService.getDataSector(sectorlist,"2020-02-19","2020-04-12","volume","monthly","2020-02-09");
		        assertNotNull(Object);//check if the object is != null
		        
		        assertEquals( true, Object instanceof ChartObjectCustom);
		    }catch(Exception e){
	       
		    	fail("got Exception");
		     }
			
			try{
			        ChartObjectCustom Object =companyService.getDataSector(sectorlist,"2020-02-19","2020-04-12","volume","monthly","2020-02-09");
			        assertNotNull(Object);//check if the object is != null
			        
			        assertEquals( true, Object instanceof ChartObjectCustom);
			    }catch(Exception e){
		       
			    	fail("got Exception");
			     }
			
		}
		
		@Test
		void testgetChart() throws ParseException {
			
			when(companyRepository.findByTicker("DMT")).thenReturn(company1);
			when(companyRepository.findByTicker("BBZ")).thenReturn(company2);
			when(companyRepository.findBySector("Retail")).thenReturn(retail);
			
			List<String> tickerlist = new ArrayList<String>();
			List<String> sectorlist = new ArrayList<String>();
					        
		    try{
			        ChartObjectCustom Object =companyService.getChart(tickerlist,sectorlist,"2020-02-19","2020-04-12","volume","monthly","both","2020-02-09");
			        assertNotNull(Object);//check if the object is != null
			        
			        assertEquals( true, Object instanceof ChartObjectCustom);
			    }catch(Exception e){
		       
			    	fail("got Exception");
			     }
		    
	        
		    try{
			        ChartObjectCustom Object =companyService.getChart(tickerlist,sectorlist,"2020-02-19","2020-04-12","price","daily","both","2020-02-09");
			        assertNotNull(Object);//check if the object is != null
			        
			        assertEquals( true, Object instanceof ChartObjectCustom);
			    }catch(Exception e){
		       
			    	fail("got Exception");
			     }
		    
	        
		    try{
			        ChartObjectCustom Object =companyService.getChart(tickerlist,sectorlist,"2020-02-19","2020-04-12","price","weekly","both","2020-02-09");
			        assertNotNull(Object);//check if the object is != null
			        
			        assertEquals( true, Object instanceof ChartObjectCustom);
			    }catch(Exception e){
		       
			    	fail("got Exception");
			     }	        
		    try{
		        ChartObjectCustom Object =companyService.getChart(tickerlist,sectorlist,"2020-02-19","2020-04-12","volume","weekly","both","2020-02-09");
		        assertNotNull(Object);//check if the object is != null
		        
		        assertEquals( true, Object instanceof ChartObjectCustom);
		    }catch(Exception e){
	       
		    	fail("got Exception");
		     }
		    
	        
			tickerlist.add("DMT");
			tickerlist.add("BBZ");	
			
		    try{
			        ChartObjectCustom Object =companyService.getChart(tickerlist,sectorlist,"2020-02-19","2020-04-12","volume","daily","company","2020-02-09");
			        assertNotNull(Object);//check if the object is != null
			        
			        assertEquals( true, Object instanceof ChartObjectCustom);
			    }catch(Exception e){
		       
			    	fail("got Exception");
			     }
		    

			sectorlist.add("Retail");	

		    try{
			        ChartObjectCustom Object =companyService.getChart(tickerlist,sectorlist,"2020-02-19","2020-04-12","price","weekly","company","2020-02-09");
			        assertNotNull(Object);//check if the object is != null
			        
			        assertEquals( true, Object instanceof ChartObjectCustom);
			    }catch(Exception e){
		       
			    	fail("got Exception");
			     }
		    
	        

		    try{
			        ChartObjectCustom Object =companyService.getChart(tickerlist,sectorlist,"2020-02-19","2020-04-12","price","monthly","both","2020-02-09");
			        assertNotNull(Object);//check if the object is != null
			        
			        assertEquals( true, Object instanceof ChartObjectCustom);
			    }catch(Exception e){
		       
			    	fail("got Exception");
			     }
		    

		    try{
			        ChartObjectCustom Object =companyService.getChart(tickerlist,sectorlist,"2020-02-19","2020-04-12","volume","covid","both","2020-02-09");
			        assertNotNull(Object);//check if the object is != null
			        
			        assertEquals( true, Object instanceof ChartObjectCustom);
			    }catch(Exception e){
		       
			    	fail("got Exception");
			     }

		    try{
			        ChartObjectCustom Object =companyService.getChart(tickerlist,sectorlist,"2020-02-19","2020-04-12","price","daily","both","2020-02-09");
			        assertNotNull(Object);//check if the object is != null
			        
			        assertEquals( true, Object instanceof ChartObjectCustom);
			    }catch(Exception e){
		       
			    	fail("got Exception");
			     }
		    
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
	void testGetGridData() throws ParseException {

		
		when(companyRepository.findByTicker("DMT")).thenReturn(company1);
		when(companyRepository.findByTicker("BBZ")).thenReturn(company2);
		
		List<String> tickerlist = new ArrayList<String>();
		tickerlist.add("DMT");
		tickerlist.add("BBZ");
		
		when(companyRepository.findBySector("Retail")).thenReturn(retail);
		

		List<String> sectorlist = new ArrayList<String>();
		sectorlist.add("Retail");
		List<DailyData> objnew=new ArrayList<>();

		DailyData dataobj=new DailyData();
		dataobj.setTicker("DMT");
		dataobj.setCompanyName("DMart");
		dataobj.setDate("06-02-2020");
		dataobj.setPrice("100");
		dataobj.setSector("Retail");
		dataobj.setVolume("50");
		

		DailyData dataobj2=new DailyData();
		dataobj2.setTicker("BBZ");
		dataobj2.setCompanyName("Future Grp");
		dataobj2.setDate("06-02-2020");
		dataobj2.setPrice("80");
		dataobj2.setSector("Retail");
		dataobj2.setVolume("40");
		
		objnew=companyService.getGridData("2020-02-06", "2020-02-06", tickerlist, sectorlist);

		assertEquals(dataobj.getCompanyName(), objnew.get(1).getCompanyName());
	    assertEquals(dataobj.getDate(),objnew.get(1).getDate());
		assertEquals(dataobj2.getVolume(),objnew.get(0).getVolume());
		assertEquals(dataobj2.getPrice(),objnew.get(0).getPrice());
		
	}
			
	}






