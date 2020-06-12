package com.restapi.market.service;

import static org.junit.jupiter.api.Assertions.*;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.OngoingStubbing;

import com.restapi.market.model.AverageValues;
import com.restapi.market.model.Calculate;
import com.restapi.market.model.ChartObject;
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
	
	Company company1, company2, company3, company4,company5,company6,company7,company8,company9;
	
	List<Company> tech_sector =new ArrayList<Company>();
	List<Company> retail = new ArrayList<Company>();
	List<Stock> stocks1 = new ArrayList<Stock>();
	List<String> sectors=new ArrayList<String>();
	List<String> tickers=new ArrayList<String>();
	Map<String,Double> VolSortedSector=new HashMap< String,Double>(); 
	Map<String,Double> PriceSortedSectors=new HashMap< String,Double>(); 
	Map<String,Double> VolSortedCompany=new HashMap< String,Double>(); 
	Map<String,Double> PriceSortedCompany=new HashMap< String,Double>(); 
	
	List<Company> courier = new ArrayList<Company>();
	
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
		
		List<Stock> stocks5 = new ArrayList<Stock>();
		stocks5.add(new Stock("2020-02-05", 110, 50, 01, "02"));
		stocks5.add(new Stock("2020-02-06", 180, 40, 01, "02"));
		stocks5.add(new Stock("2020-02-07", 80, 70, 11, "03"));
		stocks5.add(new Stock("2020-03-11", 70, 60, 11, "03"));
		stocks5.add(new Stock("2020-03-13", 110, 50, 01, "02"));
		stocks5.add(new Stock("2020-04-14", 180, 40, 01, "02"));
		stocks5.add(new Stock("2020-04-15", 80, 70, 11, "03"));
		stocks5.add(new Stock("2020-04-16", 70, 60, 11, "03"));
		
		company1 = new Company("id1", "DMart", "DMT", "Retail", stocks1);
		company2 = new Company("id2", "Future Grp", "BBZ", "Retail", stocks2);
		company3 = new Company("id3", "Blue Dart", "BDT", "Courier", stocks3);
		company4 = new Company("id4", "DHL", "DH", "Courier", stocks4);	
		company5 = new Company("id5", "APPLE INC", "AAPL", "Technology", stocks1);
		company6 = new Company("id6", "ADVANCED MICRO DEVICES", "AMD", "Technology", stocks2);
		company7 = new Company("id7", "GROUPON INC", "GRPN", "Technology", stocks3);
		company8 = new Company("id8", "GRUBHUB INC", "GRUB", "Technology", stocks4);
		company9 = new Company("id8", "ALIBABA GROUP HOLDING LTD", "BABA", "Miscellaneous", stocks5);	
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
		VolSortedSector.put("Courier", new Double(22.5)); 
		VolSortedSector.put("Retail", new Double(22.5)); 
		VolSortedSector.put("Technology", new Double(27.5)); 
		
		VolSortedCompany.put("DH", new Double(20)); 
		VolSortedCompany.put("DMT", new Double(20)); 
		VolSortedCompany.put("BDT", new Double(25)); 

		
		tickers.add("DMT");
		tickers.add("DH");
		tickers.add("BDT");
		VolSortedSector.put("Courier", new Double(22.5)); 
		VolSortedSector.put("Retail", new Double(22.5)); 
		VolSortedSector.put("Technology", new Double(27.5)); 
		
		VolSortedCompany.put("DH", new Double(20)); 
		VolSortedCompany.put("DMT", new Double(20)); 
		VolSortedCompany.put("BDT", new Double(25)); 
		

		PriceSortedCompany.put("DH", new Double(-70)); 
		PriceSortedCompany.put("BDT", new Double(-50)); 
		PriceSortedCompany.put("DMT", new Double(-50)); 
		
		PriceSortedCompany.put("Courier", new Double(-45)); 
		PriceSortedCompany.put("Technology", new Double(-31.25)); 
		PriceSortedCompany.put("Retail", new Double(72.5)); 				
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
			//assertEquals(67.5, volumeAverage.getPostCovidValue());
			//assertEquals(22.5, volumeAverage.getDeviation());
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
			AverageValues volumeAverage = companyService.CompanyAverage("BDT","volume","2020-02-09");
			assertEquals(50, volumeAverage.getPreCovidValue());
			assertEquals(75, volumeAverage.getPostCovidValue());
			assertEquals(25, volumeAverage.getDeviation());
	     }
		
		
		// 6. Calculate Average Value of the mentioned parameter (price/volume)  for a Sector
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

		/*

		
		// 7. Pre-Post Covid Deviation for Average Volume Sector
		@Test
		void testgetSectorVolumeDeviation() throws ParseException{
			
			when(companyService.getAllSectors()).thenReturn(sectors);
			when(companyRepository.findBySector("Technology")).thenReturn(tech_sector);
			when(companyRepository.findBySector("Retail")).thenReturn(retail);
			when(companyRepository.findBySector("Courier")).thenReturn(courier);
			
			when(companyRepository.findByTicker(company1.getTicker())).thenReturn(company1);
			when(companyRepository.findByTicker(company2.getTicker())).thenReturn(company2);
			when(companyRepository.findByTicker(company3.getTicker())).thenReturn(company3);
			when(companyRepository.findByTicker(company4.getTicker())).thenReturn(company4);
			when(companyRepository.findByTicker(company5.getTicker())).thenReturn(company5);
			when(companyRepository.findByTicker(company6.getTicker())).thenReturn(company6);
			when(companyRepository.findByTicker(company7.getTicker())).thenReturn(company7);
			when(companyRepository.findByTicker(company8.getTicker())).thenReturn(company8);
			
			Map<String,Double> sorted_volume_sectors=companyService.getSectorVolumeDeviation("2020-02-10");
			assertEquals(VolSortedSector, sorted_volume_sectors);
		}
		
		
		// 8. Pre-Post Covid Deviation for Average Price Sector
		@Test
		void testgetSectorPriceDeviation() throws ParseException{
			
			when(companyService.getAllSectors()).thenReturn(sectors);
			when(companyRepository.findBySector("Technology")).thenReturn(tech_sector);
			when(companyRepository.findBySector("Retail")).thenReturn(retail);
			when(companyRepository.findBySector("Courier")).thenReturn(courier);
			
			when(companyRepository.findByTicker(company1.getTicker())).thenReturn(company1);
			when(companyRepository.findByTicker(company2.getTicker())).thenReturn(company2);
			when(companyRepository.findByTicker(company3.getTicker())).thenReturn(company3);
			when(companyRepository.findByTicker(company4.getTicker())).thenReturn(company4);
			when(companyRepository.findByTicker(company5.getTicker())).thenReturn(company5);
			when(companyRepository.findByTicker(company6.getTicker())).thenReturn(company6);
			when(companyRepository.findByTicker(company7.getTicker())).thenReturn(company7);
			when(companyRepository.findByTicker(company8.getTicker())).thenReturn(company8);
			
			Map<String,Double> sorted_price_sectors=companyService.getSectorPriceDeviation("2020-02-10");
			assertEquals(PriceSortedSectors, sorted_price_sectors);
		}
		
		
		// 9. Pre-Post Covid Deviation for Average Volume Company
		@Test
		void testgetCompanyVolumeDeviation()throws ParseException{
			
			when(companyService.getAllTickers()).thenReturn(tickers);
			when(companyRepository.findByTicker("DMT")).thenReturn(company1);
			when(companyRepository.findByTicker("DH")).thenReturn(company4);
			when(companyRepository.findByTicker("BDT")).thenReturn(company3);
			
			Map<String,Double> sorted_volume_company=companyService.getCompanyVolumeDeviation("2020-02-10");
			assertEquals(VolSortedCompany, sorted_volume_company);
		}
		
		// 10. Pre-Post Covid Deviation for Average Price Company
		@Test
		void testgetCompanyPriceDeviation()throws ParseException{
			
			when(companyService.getAllTickers()).thenReturn(tickers);
			when(companyRepository.findByTicker("DMT")).thenReturn(company1);
			when(companyRepository.findByTicker("DH")).thenReturn(company4);
			when(companyRepository.findByTicker("BDT")).thenReturn(company3);
			
			Map<String,Double> sorted_price_company=companyService.getCompanyPriceDeviation("2020-02-10");
			assertEquals(PriceSortedCompany.get("DMT"), sorted_price_company.get("DMT"));
			assertEquals(PriceSortedCompany.get("DH"), sorted_price_company.get("DH"));
			assertEquals(PriceSortedCompany.get("BDT"), sorted_price_company.get("BDT"));
		}

		*/


		//11. Avg for selected dates ---- function implemented for summary line
		@Test
		void testgetDataByRangeCompany() throws ParseException{
			 Calculate cal=new Calculate();
			 when(companyRepository.findByTicker(anyString())).thenReturn(company4);
			 cal = companyService.getDataByRangeCompany("DH","2020-02-06","2020-03-11");
			assertEquals(55,cal.getVolume());
			assertEquals(110,cal.getPrice());
		}
		
		//12. Avg for selected dates ---- function implemented for summary line
		@Test
		void testgetDataByRangeSector() throws ParseException{
			 Calculate cal=new Calculate();
			 when(companyRepository.findBySector(anyString())).thenReturn(tech_sector);
			 cal = companyService.getDataByRangeSector("Technology","2020-02-06","2020-03-11");
			assertEquals(56.25,cal.getVolume());
			assertEquals(101.875,cal.getPrice());
		}
		
		// 13. Avg calculation for list of stocks
		
		
		@Test
		void testAverageStock() {

			when(companyRepository.findByTicker("BBZ")).thenReturn(company2);
			List<Stock>stocks = company2.getStocks();
			try{
		        Calculate Object =companyService.averagestock(stocks);
		        assertNotNull(Object);//check if the object is != null
		        
		        assertEquals( true, Object instanceof Calculate);
		    }catch(Exception e){
	       
		        fail("got Exception");
		     }
		}
		
		
	////////// SECTORS 
		
		@Test
		void testMonthlySector() throws ParseException {
			when(companyRepository.findBySector("Retail")).thenReturn(retail);
			Map<String, Double> avgMap = companyService.MonthlySector("Retail", "2020-01-02", "2020-06-01", "price");
			assertEquals(107.5, avgMap.get("02"));
			assertEquals(90, avgMap.get("03"));
			avgMap = companyService.MonthlySector("Retail", "2020-01-02", "2020-06-01", "volume");
			assertEquals(42.5, avgMap.get("02"));
			assertEquals(65, avgMap.get("03"));
			avgMap = companyService.MonthlySector("Retail", "2020-01-02", "2020-06-01", "stonks");		
			assertEquals(null, avgMap);		
		}
		
		@Test
		void testWeeklySector() throws ParseException {
			when(companyRepository.findBySector("Retail")).thenReturn(retail);	
			Map<Integer, Double> weeklysector = companyService.WeeklySector("Retail", "2020-01-02", "2020-06-01", "price");
			assertEquals(107.5, weeklysector.get(01));	
			weeklysector  = companyService.WeeklySector("Retail", "2020-01-02", "2020-06-01", "volume");
			assertEquals(42.5, weeklysector.get(1));
			assertEquals(65, weeklysector.get(11));
			weeklysector = companyService.WeeklySector("Retail", "2020-01-02", "2020-06-01", "stonks");		
			assertEquals(null, weeklysector);		
		}
		
		
		@Test
		void testDailySector() throws ParseException {
			when(companyRepository.findBySector("Retail")).thenReturn(retail);
			Map<String, Double> avgMap = companyService.DailySector("Retail", "2020-01-02", "2020-06-01", "price");
			assertEquals(90, avgMap.get("2020-02-06"));
			assertEquals(80, avgMap.get("2020-03-10"));
			avgMap = companyService.DailySector("Retail", "2020-01-02", "2020-06-01", "volume");
			assertEquals(45, avgMap.get("2020-02-06"));
			assertEquals(70, avgMap.get("2020-03-10"));
			avgMap = companyService.DailySector("Retail", "2020-01-02", "2020-06-01", "stonks");		
			assertEquals(null, avgMap);	
		}
		
		
	/////////// COMPANY 	
				
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
		Map<Integer, Double> avgMap = companyService.WeeklyCompany("BBZ", "2020-01-02", "2020-06-01", "price");
		assertEquals(105, avgMap.get(1));
		assertEquals(90, avgMap.get(11));
		avgMap = companyService.WeeklyCompany("BBZ", "2020-01-02", "2020-06-01", "volume");
		assertEquals(40, avgMap.get(1));
		assertEquals(65, avgMap.get(11));
		avgMap = companyService.WeeklyCompany("BBZ", "2020-01-02", "2020-06-01", "stonks");		
		assertEquals(null, avgMap);		
	}
	
	@Test
	void testDailyCompany() throws ParseException {
		when(companyRepository.findByTicker("DMT")).thenReturn(company1);
		Map<String, Double> avgMap = companyService.DailyCompany("DMT", "2020-01-02", "2020-06-01", "price");
		assertEquals(100, avgMap.get("2020-02-06"));
		assertEquals(120, avgMap.get("2020-02-07"));
		avgMap = companyService.DailyCompany("DMT", "2020-01-02", "2020-06-01", "volume");
		assertEquals(50, avgMap.get("2020-02-06"));
		assertEquals(40, avgMap.get("2020-02-07"));
		avgMap = companyService.DailyCompany("DMT", "2020-01-02", "2020-06-01", "stonks");		
		assertEquals(null, avgMap);	
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
		
		List<DailyData>exp_obj = new ArrayList<>();
		DailyData obj1 = new DailyData("DMart", "Retail" , "DMT","100", "50", "2020-06-02");
		DailyData obj2 = new DailyData("DMart", "Retail" , "DMT","120", "40", "2020-07-02");
		DailyData obj3 = new DailyData("DMart", "Retail" , "DMT","80", "70", "2020-10-02");
		DailyData obj4 = new DailyData("DMart", "Retail" , "DMT","100", "60", "2020-11-02");
		exp_obj.add(obj1);
		exp_obj.add(obj2);
		exp_obj.add(obj3);
		exp_obj.add(obj4);
		
		assertEquals(true, companyService.getGridData("2020-06-02","2020-11-02", tickerlist, sectorlist));
		
	}
		
	
	@Test
	void testGetChartCompany() throws ParseException {
		when(companyRepository.findByTicker("DMT")).thenReturn(company1);
		when(companyRepository.findByTicker("BBZ")).thenReturn(company2);
		List<String> tickerlist = new ArrayList<String>();
		tickerlist.add("DMT");
		tickerlist.add("BBZ");


		ChartObject obj1 = new ChartObject ("DMart",new ArrayList<Double>(){{add(45.89);add(98.89);}},"#FF0088","FF8800",false);
		ChartObject obj2 = new ChartObject ("DMart",new ArrayList<Double>(){{add(45.89);add(98.89);}},"#000088","008800",false);
		ChartObject obj3 = new ChartObject ("DMart",new ArrayList<Double>(){{add(45.89);add(98.89);}},"#880088","888800",false);
			
		List<ChartObject> value;
		value = companyService.getChartCompany(tickerlist,"volume","2020-09-02");
		
		assertEquals(obj1,value.get(0));
		assertEquals(obj2,value.get(1));
		assertEquals(obj3,value.get(2));	


		//ChartObject obj1 = new ChartObject ("DMart",new ArrayList<Double>(){{add(45.89);add(98.89);}},"#FF0088","FF8800",false);
		//ChartObject obj2 = new ChartObject ("DMart",new ArrayList<Double>(){{add(45.89);add(98.89);}},"#000088","008800",false);
		//ChartObject obj3 = new ChartObject ("DMart",new ArrayList<Double>(){{add(45.89);add(98.89);}},"#880088","888800",false);
		
		
		assertEquals(obj1,value.get(0));
		assertEquals(obj2,value.get(1));
		assertEquals(obj3,value.get(2));
		
		
		/*try{

			List<ChartObject> value=new ArrayList<ChartObject> ();
			value = companyService.getChartCompany(tickerlist,"volume","2020-09-02");
			
	        assertNotNull(value);//check if the object is != null	        
	        assertEquals( true, value instanceof ChartObject);
	    }catch(Exception e){    
	        fail("got Exception");
	     }	*/

	
	}

	
 	/*	@Test
	void testAddStocksByTicker() throws ParseException {
		when(companyRepository.findByTicker("DMT")).thenReturn(company1);
		
		String ans = companyService.addStocksByTicker("DMT");
		assertEquals(ans,"DMT information added to DB");
	}
	 */

	/////////////////////////////// CHARTS DAILY WEEKLY MONTHLY ////////////////////////////
	
	///// A.	DAILY
	//1. ONLY COMPANY 
	@Test
	void testDailyCompanyObject() {
		when(companyRepository.findByTicker("DMT")).thenReturn(company1);
		when(companyRepository.findByTicker("BBZ")).thenReturn(company2);
		when(companyRepository.findBySector("Retail")).thenReturn(retail);
		List<String> tickerlist = new ArrayList<String>();
		tickerlist.add("DMT");
		tickerlist.add("BBZ");		
		List<String> sectorlist = new ArrayList<String>();
		sectorlist.add("Retail");	
		try{
	        ChartObjectCustom Object =companyService.DailyCompanyObject(tickerlist,"2020-02-19","2020-04-12","price");
	        assertNotNull(Object);//check if the object is != null	        
	        assertEquals( true, Object instanceof ChartObjectCustom);
	    }catch(Exception e){    
	        fail("got Exception");
	     }	
	}	
	
	// 2. ONLY SECTOR
	@Test
	void testDailySectorObject() {
		
		when(companyRepository.findByTicker("DMT")).thenReturn(company1);
		when(companyRepository.findByTicker("BBZ")).thenReturn(company2);
		when(companyRepository.findBySector("Retail")).thenReturn(retail);
		List<String> sectorlist = new ArrayList<String>();
		sectorlist.add("Retail");
		try{
	        ChartObjectCustom Object =companyService.DailyCompanyObject(sectorlist,"2020-02-19","2020-04-12","price");
	        assertNotNull(Object);//check if the object is != null
	        
	        assertEquals( true, Object instanceof ChartObjectCustom);
	    }catch(Exception e){
       
	    	//fail("got Exception");
	     }
	}
	
	// 3. SECTOR-COMPANY RETURN COMPANY
	@Test
	void testDailyCompanySectorObject() {
		when(companyRepository.findByTicker("DMT")).thenReturn(company1);
		when(companyRepository.findByTicker("BBZ")).thenReturn(company2);
		when(companyRepository.findBySector("Retail")).thenReturn(retail);
		List<String> tickerlist = new ArrayList<String>();
		tickerlist.add("DMT");
		tickerlist.add("BBZ");
		
		List<String> sectorlist = new ArrayList<String>();
		sectorlist.add("Retail");
		
		try{
	        ChartObjectCustom Object =companyService.DailyCompanySectorObject(tickerlist,sectorlist,"2020-02-19","2020-04-12","price");
	        assertNotNull(Object);//check if the object is != null
	        
	        assertEquals( true, Object instanceof ChartObjectCustom);
	    }catch(Exception e){
       
	    	fail("got Exception");
	     }
	
	}
	
	// 4. SECTOR-COMPANY RETURN COMPANY AND SECTOR 
	@Test
	void testDailyAvgCompanySectorObject() {
		when(companyRepository.findByTicker("DMT")).thenReturn(company1);
		when(companyRepository.findByTicker("BBZ")).thenReturn(company2);
		when(companyRepository.findBySector("Retail")).thenReturn(retail);
		List<String> tickerlist = new ArrayList<String>();
		tickerlist.add("DMT");
		tickerlist.add("BBZ");	
		List<String> sectorlist = new ArrayList<String>();
		sectorlist.add("Retail");	
		try{
	        ChartObjectCustom Object =companyService.DailyAvgCompanySectorObject(tickerlist,sectorlist,"2020-02-19","2020-04-12","price");
	        assertNotNull(Object);//check if the object is != null
	        
	        assertEquals( true, Object instanceof ChartObjectCustom);
	    }catch(Exception e){     
	    	fail("got Exception");
	    }	
	}
	
	
	///// B. MONTHLY
	
	// 1. ONLY COMPANY
	
	@Test
	void testMonthlyCompanyObject() {
		when(companyRepository.findByTicker("DMT")).thenReturn(company1);
		when(companyRepository.findByTicker("BBZ")).thenReturn(company2);
		List<String> tickerlist = new ArrayList<String>();
		tickerlist.add("DMT");
		tickerlist.add("BBZ");		
		List<String> sectorlist = new ArrayList<String>();
		sectorlist.add("Retail");	
		try{
	        ChartObjectCustom Object =companyService.MonthlyCompanyObject(tickerlist,"2020-02-19","2020-04-12","price");
	        assertNotNull(Object);//check if the object is != null	        
	        assertEquals( true, Object instanceof ChartObjectCustom);
	    }catch(Exception e){    
	    	fail("got Exception");
	     }	
	}	
	
	// 2. ONLY SECTOR 
	@Test
	void testMonthlySectorObject() {
		when(companyRepository.findByTicker("DMT")).thenReturn(company1);
		when(companyRepository.findByTicker("BBZ")).thenReturn(company2);
		when(companyRepository.findBySector("Retail")).thenReturn(retail);
		List<String> sectorlist = new ArrayList<String>();
		sectorlist.add("Retail");
		try{
	        ChartObjectCustom Object =companyService.MonthlyCompanyObject(sectorlist,"2020-02-19","2020-04-12","price");
	        assertNotNull(Object);//check if the object is != null
	        
	        assertEquals( true, Object instanceof ChartObjectCustom);
	    }catch(Exception e){
       
	    	System.out.print("Object not found");
	     }
	}
	
	
	// 3. COMPANY-SECTOR RETURN ONLY COMPANY
	
	@Test
	void testMonthlyCompanySectorObject() {
		when(companyRepository.findByTicker("DMT")).thenReturn(company1);
		when(companyRepository.findByTicker("BBZ")).thenReturn(company2);
		when(companyRepository.findBySector("Retail")).thenReturn(retail);
		List<String> tickerlist = new ArrayList<String>();
		tickerlist.add("DMT");
		tickerlist.add("BBZ");
		
		List<String> sectorlist = new ArrayList<String>();
		sectorlist.add("Retail");
		
		try{
	        ChartObjectCustom Object =companyService.MonthlyCompanySectorObject(tickerlist,sectorlist,"2020-02-19","2020-04-12","price");
	        assertNotNull(Object);//check if the object is != null
	        
	        assertEquals( true, Object instanceof ChartObjectCustom);
	    }catch(Exception e){
       
	    	fail("got Exception");
	     }
	
	}
	
	// 4. SECTOR-COMPANY RETURN COMPANY AND SECTOR
	@Test
	void testMonthlyAvgCompanySectorObject() {
		when(companyRepository.findByTicker("DMT")).thenReturn(company1);
		when(companyRepository.findByTicker("BBZ")).thenReturn(company2);
		when(companyRepository.findBySector("Retail")).thenReturn(retail);
		List<String> tickerlist = new ArrayList<String>();
		tickerlist.add("DMT");
		tickerlist.add("BBZ");	
		List<String> sectorlist = new ArrayList<String>();
		sectorlist.add("Retail");	
		try{
	        ChartObjectCustom Object =companyService.MonthlyAvgCompanySectorObject(tickerlist,sectorlist,"2020-02-19","2020-04-12","price");
	        assertNotNull(Object);//check if the object is != null
	        
	        assertEquals( true, Object instanceof ChartObjectCustom);
	    }catch(Exception e){     
	    	fail("got Exception");
	    }	
	}
	
	
	////// C. WEEKLY
	
	//1. ONLY COMPANY
	@Test
	void testWeeklyCompanyObject() {
		when(companyRepository.findByTicker("DMT")).thenReturn(company1);
		when(companyRepository.findByTicker("BBZ")).thenReturn(company2);
		
		List<String> tickerlist = new ArrayList<String>();
		tickerlist.add("DMT");
		tickerlist.add("BBZ");		
		List<String> sectorlist = new ArrayList<String>();
		sectorlist.add("Retail");	
		try{
	        ChartObjectCustom Object =companyService.WeeklyCompanyObject(tickerlist,"2020-02-19","2020-04-12","price");
	        assertNotNull(Object);//check if the object is != null	        
	        assertEquals( true, Object instanceof ChartObjectCustom);
	    }catch(Exception e){    
	    	fail("got Exception");
	     }	
	}
	
	// 2. ONLY SECTOR
	@Test
	void testWeeklySectorObject() {
		when(companyRepository.findByTicker("DMT")).thenReturn(company1);
		when(companyRepository.findByTicker("BBZ")).thenReturn(company2);
		when(companyRepository.findBySector("Retail")).thenReturn(retail);
		List<String> sectorlist = new ArrayList<String>();
		sectorlist.add("Retail");
		try{
	        ChartObjectCustom Object =companyService.WeeklyCompanyObject(sectorlist,"2020-02-19","2020-04-12","price");
	        assertNotNull(Object);//check if the object is != null
	        
	        assertEquals( true, Object instanceof ChartObjectCustom);
	    }
		catch(Exception e){
       
	    	System.out.print("Object not found");
	     }
	}
	
	// 3. SECTOR-COMPANY RETURN COMPANY
	@Test
	void testWeeklyCompanySectorObject() {
		when(companyRepository.findByTicker("DMT")).thenReturn(company1);
		when(companyRepository.findByTicker("BBZ")).thenReturn(company2);
		when(companyRepository.findBySector("Retail")).thenReturn(retail);
		List<String> tickerlist = new ArrayList<String>();
		tickerlist.add("DMT");
		tickerlist.add("BBZ");
		
		List<String> sectorlist = new ArrayList<String>();
		sectorlist.add("Retail");
		
		try{
	        ChartObjectCustom Object =companyService.WeeklyCompanySectorObject(tickerlist,sectorlist,"2020-02-19","2020-04-12","price");
	        assertNotNull(Object);//check if the object is != null
	        
	        assertEquals( true, Object instanceof ChartObjectCustom);
	    }catch(Exception e){
       
	    	fail("got Exception");
	     }
	
	}

	// 4. SECTOR-COMPANY RETURN COMPANY-SECTOR
	@Test
	void testWeeklyAvgCompanySectorObject() throws ParseException {
		when(companyRepository.findByTicker("DMT")).thenReturn(company1);
		when(companyRepository.findByTicker("BBZ")).thenReturn(company2);
		when(companyRepository.findBySector("Retail")).thenReturn(retail);
		List<String> tickerlist = new ArrayList<String>();
		tickerlist.add("DMT");
		tickerlist.add("BBZ");	
		List<String> sectorlist = new ArrayList<String>();
		sectorlist.add("Retail");	
		
	        ChartObjectCustom Object =companyService.WeeklyAvgCompanySectorObject(tickerlist,sectorlist,"2020-02-19","2020-04-12","price");
	        assertNotNull(Object);//check if the object is != null        
	        assertEquals( true, Object instanceof ChartObjectCustom);	   	
	}

}
































































