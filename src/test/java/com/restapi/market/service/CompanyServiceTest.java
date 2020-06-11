package com.restapi.market.service;

import static org.junit.jupiter.api.Assertions.*;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
	
	Company company1, company2, company3, company4;
	List<Company> retail = new ArrayList<Company>();
	List<Company> courier = new ArrayList<Company>();

	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		List<Stock> stocks1 = new ArrayList<Stock>();
		stocks1.add(new Stock("2020-02-06", 100, 50, 1, "02"));
		stocks1.add(new Stock("2020-02-07",120, 40, 1, "02"));
		stocks1.add(new Stock("2020-03-10", 80, 70, 11, "03"));
		stocks1.add(new Stock("2020-03-11",100, 60, 11, "03"));
		
		List<Stock> stocks2 = new ArrayList<Stock>();
		stocks2.add(new Stock("2020-02-06", 80, 40, 1, "02"));
		stocks2.add(new Stock("2020-02-07",  130, 40, 1, "02"));
		stocks2.add(new Stock("2020-03-10",80, 70, 11, "03"));
		stocks2.add(new Stock("2020-03-11", 100, 60, 11, "03"));
		
		List<Stock> stocks3 = new ArrayList<Stock>();
		stocks3.add(new Stock("2020-02-06",  120, 60, 1, "02"));
		stocks3.add(new Stock("2020-02-07", 100, 40, 1, "02"));
		stocks3.add(new Stock("2020-03-10",  80, 90, 11, "03"));
		stocks3.add(new Stock("2020-03-11",  100, 60, 11, "03"));
		
		List<Stock> stocks4 = new ArrayList<Stock>();
		stocks4.add(new Stock("2020-02-06",  110, 50, 1, "02"));
		stocks4.add(new Stock("2020-02-07",  180, 40, 1, "02"));
		stocks4.add(new Stock("2020-03-10",  80, 70, 11, "03"));
		stocks4.add(new Stock("2020-03-11",  70, 60, 11, "03"));
		
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
		assertEquals(100, avgMap.get("2020-06-02"));
		assertEquals(120, avgMap.get("2020-07-02"));
		avgMap = companyService.DailyCompany("DMT", "2020-01-02", "2020-06-01", "volume");
		assertEquals(45, avgMap.get("2020-01-02"));
		assertEquals(65, avgMap.get("2020-01-02"));
		avgMap = companyService.DailyCompany("DMT", "2020-01-02", "2020-06-01", "stonks");		
		assertEquals(null, avgMap);	
	}
	
	@Test
	void testMonthlySector() throws ParseException {
		when(companyRepository.findBySector("Retail")).thenReturn(retail);
		Map<String, Double> avgMap = companyService.MonthlySector("Retail", "2020-01-02", "2020-06-01", "price");
		assertEquals(110, avgMap.get("02"));
		assertEquals(90, avgMap.get("03"));
		avgMap = companyService.MonthlySector("Retail", "2020-01-02", "2020-06-01", "volume");
		assertEquals(45, avgMap.get("02"));
		assertEquals(65, avgMap.get("03"));
		avgMap = companyService.MonthlySector("Retail", "2020-01-02", "2020-06-01", "stonks");		
		assertEquals(null, avgMap);		
	}
	
	@Test
	void testWeeklySector() throws ParseException {
		when(companyRepository.findBySector("Retail")).thenReturn(retail);
		Map<Integer, Double> avgMap = companyService.WeeklySector("Retail", "2020-01-02", "2020-06-01", "price");
		assertEquals(105, avgMap.get(1));
		assertEquals(90, avgMap.get(11));
		avgMap = companyService.WeeklySector("Retail", "2020-01-02", "2020-06-01", "volume");
		assertEquals(40, avgMap.get(1));
		assertEquals(65, avgMap.get(11));
		avgMap = companyService.WeeklySector("Retail", "2020-01-02", "2020-06-01", "stonks");		
		assertEquals(null, avgMap);		
	}
	
	
	@Test
	void testDailySector() throws ParseException {
		when(companyRepository.findBySector("Retail")).thenReturn(retail);
		Map<String, Double> avgMap = companyService.DailySector("Retail", "2020-01-02", "2020-06-01", "price");
		assertEquals(100, avgMap.get("2020-06-02"));
		assertEquals(120, avgMap.get("2020-07-02"));
		avgMap = companyService.DailySector("Retail", "2020-01-02", "2020-06-01", "volume");
		assertEquals(45, avgMap.get("2020-01-02"));
		assertEquals(65, avgMap.get("2020-01-02"));
		avgMap = companyService.DailySector("Retail", "2020-01-02", "2020-06-01", "stonks");		
		assertEquals(null, avgMap);	
	}
	
		
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
	
	@Test
	void testGetSectorVolumeDeviation() throws ParseException {	
		when(companyRepository.findBySector("Retail")).thenReturn(retail);
		when(companyRepository.findBySector("Courier")).thenReturn(courier);

		when(companyRepository.findBySector("Retail")).thenReturn(retail);
		Map<String,Double>volMap = companyService.getSectorVolumeDeviation("2020-09-02");
		assertEquals(-45.67,volMap.get("Retail"));
		assertEquals(55.67,volMap.get("Courier"));		
	}
	
	@Test
	void testGetSectorPriceDeviation() throws ParseException {
		
		when(companyRepository.findBySector("Retail")).thenReturn(retail);
		when(companyRepository.findBySector("Courier")).thenReturn(courier);
		Map<String,Double>priceMap = companyService.getSectorPriceDeviation("2020-09-02");
		assertEquals(-17.5,priceMap.get("Retail"));
		assertEquals(-40,priceMap.get("Courier"));		
	}
	
	@Test
	void testGetCompanyVolumeDeviation() throws ParseException {
		
		when(companyRepository.findByTicker("DMT")).thenReturn(company1);
		when(companyRepository.findByTicker("BBZ")).thenReturn(company2);
		List<String>tickers = new ArrayList<>();
		tickers.add("DMT");
		tickers.add("BBZ");
		
		when(companyService.getAllTickers()).thenReturn(tickers);
		
		Map<String,Double>priceMap = companyService.getCompanyVolumeDeviation("2020-09-02");
		assertEquals(-45.67,priceMap.get("DMT"));
		assertEquals(55.67,priceMap.get("BBZ"));
	}
	
	@Test
	void testGetCompanyPriceDeviation() throws ParseException {
		
		when(companyRepository.findByTicker("DMT")).thenReturn(company1);
		when(companyRepository.findByTicker("BBZ")).thenReturn(company2);
		Map<String,Double>priceMap = companyService.getCompanyPriceDeviation("2020-09-02");
		assertEquals(-45.67,priceMap.get("DMT"));
		assertEquals(55.67,priceMap.get("BBZ"));		
	}
	
	
	@Test
	void testGetDataByRangeCompany() throws ParseException  {		
		when(companyRepository.findByTicker("DMT")).thenReturn(company1);
		Calculate priceMap = companyService.getDataByRangeCompany("DMT","2020-09-02","2020-19-03");
		assertEquals(-45.67,priceMap.getPrice());
		assertEquals(55.67,priceMap.getVolume());		
	}
	
	
	@Test
	void testGetDataByRangeSector() throws ParseException  {		
		when(companyRepository.findBySector("Retail")).thenReturn(retail);
		Calculate priceMap = companyService.getDataByRangeSector("Retail","2020-09-02","2020-19-03");
		assertEquals(-45.67,priceMap.getPrice());
		assertEquals(55.67,priceMap.getVolume());		
	}
	
	
	@Test
	void testGetGridData() throws ParseException {

		List<String> tickerlist = new ArrayList<String>();
		tickerlist.add("DMT");
		tickerlist.add("BBZ");
		

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
		
		assertEquals(companyService.getGridData("2020-06-02","2020-11-02", tickerlist, sectorlist),exp_obj);
		
	}
		
	
	@Test
	void testGetChartCompany() throws ParseException {
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
		
	}

	
	@Test
	void testAddStocksByTicker() throws ParseException {
		when(companyRepository.findByTicker("DMT")).thenReturn(company1);
		String ans = companyService.addStocksByTicker("DMT");
		assertEquals(ans,"DMT information added to DB");
	}


	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/////////////////////////////// CHARTS DAILY WEEKLY MONTHLY ////////////////////////////
	@Test
	void testDailyCompanyObject() {
		
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
	@Test
	void testDailySectorObject() {
		
		List<String> sectorlist = new ArrayList<String>();
		sectorlist.add("Retail");
		try{
	        ChartObjectCustom Object =companyService.DailyCompanyObject(sectorlist,"2020-02-19","2020-04-12","price");
	        assertNotNull(Object);//check if the object is != null
	        
	        assertEquals( true, Object instanceof ChartObjectCustom);
	    }catch(Exception e){
       
	    	fail("got Exception");
	     }
	}
	
	@Test
	void testDailyCompanySectorObject() {
		
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
	
	
	@Test
	void testDailyAvgCompanySectorObject() {
		
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
	
	

	@Test
	void testMonthlyCompanyObject() {
		
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
	@Test
	void testMonthlySectorObject() {
		
		List<String> sectorlist = new ArrayList<String>();
		sectorlist.add("Retail");
		try{
	        ChartObjectCustom Object =companyService.MonthlyCompanyObject(sectorlist,"2020-02-19","2020-04-12","price");
	        assertNotNull(Object);//check if the object is != null
	        
	        assertEquals( true, Object instanceof ChartObjectCustom);
	    }catch(Exception e){
       
	    	fail("got Exception");
	     }
	}
	
	@Test
	void testMonthlyCompanySectorObject() {
		
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
	
	
	@Test
	void testMonthlyAvgCompanySectorObject() {
		
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
	
	

	@Test
	void testWeeklyCompanyObject() {
		
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
	@Test
	void testWeeklySectorObject() {
		
		List<String> sectorlist = new ArrayList<String>();
		sectorlist.add("Retail");
		try{
	        ChartObjectCustom Object =companyService.WeeklyCompanyObject(sectorlist,"2020-02-19","2020-04-12","price");
	        assertNotNull(Object);//check if the object is != null
	        
	        assertEquals( true, Object instanceof ChartObjectCustom);
	    }catch(Exception e){
       
	    	fail("got Exception");
	     }
	}
	
	@Test
	void testWeeklyCompanySectorObject() {
		
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
	
	
	@Test
	void testWeeklyAvgCompanySectorObject() {
		
		List<String> tickerlist = new ArrayList<String>();
		tickerlist.add("DMT");
		tickerlist.add("BBZ");	
		List<String> sectorlist = new ArrayList<String>();
		sectorlist.add("Retail");	
		try{
	        ChartObjectCustom Object =companyService.WeeklyAvgCompanySectorObject(tickerlist,sectorlist,"2020-02-19","2020-04-12","price");
	        assertNotNull(Object);//check if the object is != null
	        
	        assertEquals( true, Object instanceof ChartObjectCustom);
	    }catch(Exception e){     
	    	fail("got Exception");
	    }	
	}
		
	
	
}
