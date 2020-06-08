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

import com.restapi.market.model.AverageValues;
import com.restapi.market.model.Calculate;
import com.restapi.market.model.ChartObject;
import com.restapi.market.model.ChartObjectCustom;
import com.restapi.market.model.Company;
import com.restapi.market.model.DailyData;
import com.restapi.market.model.PriceAverage;
import com.restapi.market.model.ChartObjectCustom;
import com.restapi.market.model.Stock;
import com.restapi.market.model.VolumeAverage;
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

	@GetMapping("/weekly/sectorcompany/{tickers}/{sectors}/{type}/{startdate}/{enddate}")
	public ChartObjectCustom weeklytest(@PathVariable("tickers") List<String> tickers,@PathVariable("sectors") List<String> sectorlist,@PathVariable("type") String type,@PathVariable("startdate") String startdate,@PathVariable("enddate") String enddate) throws ParseException{
		return companyService. getChartCompanySectorWeekly(tickers,sectorlist,type,startdate,enddate);
	}
	

	@GetMapping("/weekly/Avgsectorcompany/{tickers}/{sectors}/{type}/{startdate}/{enddate}")
	public ChartObjectCustom weeklytestaverage(@PathVariable("tickers") List<String> tickers,@PathVariable("sectors") List<String> sectorlist,@PathVariable("type") String type,@PathVariable("startdate") String startdate,@PathVariable("enddate") String enddate) throws ParseException{
		return companyService.getAvgChartCompanySectorWeekly(tickers,sectorlist,type,startdate,enddate);
	}
	
	
	@GetMapping("/weekly/sector/{sectors}/{type}/{startdate}/{enddate}")
	public ChartObjectCustom weeklytestsector(@PathVariable("sectors") List<String> sectorlist,@PathVariable("type") String type,@PathVariable("startdate") String startdate,@PathVariable("enddate") String enddate) throws ParseException{
		return companyService.getChartSectorWeekly(sectorlist,type,startdate,enddate);
	}
	
	@GetMapping("/weekly/compoany/{tickers}/{type}/{startdate}/{enddate}")
	public ChartObjectCustom weeklytestcompany(@PathVariable("tickers") List<String> tickers,@PathVariable("type") String type,@PathVariable("startdate") String startdate,@PathVariable("enddate") String enddate) throws ParseException{
		return companyService.getChartCompanyWeekly(tickers,type,startdate,enddate);
	}
	
	// Details of company based on the ticker
	@GetMapping("/{ticker}")
	public Company getCompany(@PathVariable("ticker") String ticker) throws ParseException {
		return companyService.getByTicker(ticker);
	}

	// List of all tickers
	@GetMapping("/tickers")
	public List<String> getTickerList() {
		return companyService.getAllTickers();
	}

	// List of all sectors
	@GetMapping("/sectors")
	public List<String> getSectorList() {
		return companyService.getAllSectors();
	}

	// Add data according to the ticker
	@GetMapping("/add/{ticker}")
	public String addStocksByTicker(@PathVariable("ticker") String ticker) throws ParseException {
		return companyService.addStocksByTicker(ticker);
	}

	// Populate Database
	@GetMapping("/seed")
	public String populateDb() {
		return companyService.seedDb();
	}

	// Modify data according to the ticker
	@GetMapping("/update/{ticker}")
	public void updateByTicker(@PathVariable("ticker") String ticker) throws ParseException {
		companyService.updateByTicker(ticker);
	}

	// Update data
	@GetMapping("/force-update")
	public void forceUpdate() {
		companyService.dailyUpdateAll();
	}

	// Sorted values of Deviation Price or Volume for a company
	@GetMapping("/sort/company")
	public Map<String, Double> getDeviationCompany(@RequestParam("rank") String rank) {
		return companyService.getDeviationCompany(rank);
	}

	// Sorted values of Deviation Price or Volume for a Sector
	@GetMapping("/sort/sector")
	public Map<String, Double> getDeviationSector(@RequestParam("rank") String rank) {
		return companyService.getDeviationSector(rank);
	}

	// For a range of date average price,volume for a company
	@GetMapping("/detailsCompany/{ticker}/{startDate}/{endDate}")
	public Calculate DataCompany(@PathVariable("ticker") String ticker, @PathVariable("startDate") String startDate,
			@PathVariable("endDate") String endDate) throws ParseException {
		return companyService.getDataByRangeCompany(ticker, startDate, endDate);
	}

	// For a range of date average price,volume for a sector
	@GetMapping("/detailsSector/{sector}/{startDate}/{endDate}")
	public Calculate DataSector(@PathVariable("sector") String sector, @PathVariable("startDate") String startDate,
			@PathVariable("endDate") String endDate) throws ParseException {
		return companyService.getDataByRangeSector(sector, startDate, endDate);
	}

	// Daily, Weekly, Monthly Average Stock Price/Volume for a company
	@GetMapping("/company/{ticker}/{startDate}/{endDate}")
	public Map<String, Double> DataCompany(@PathVariable("ticker") String ticker,
			@PathVariable("startDate") String startDate, @PathVariable("endDate") String endDate,
			@RequestParam("type") String type, @RequestParam("range") String range) throws ParseException {
		return companyService.DataCompany(ticker, startDate, endDate, type, range);
	}

	// Daily, Weekly, Monthly Average Stock Price/Volume for a sector
	@GetMapping("/sector/{sector}/{startDate}/{endDate}")
	public Map<String, Double> DataSector(@PathVariable("sector") String sector,
			@PathVariable("startDate") String startDate, @PathVariable("endDate") String endDate,
			@RequestParam("type") String type, @RequestParam("range") String range) throws ParseException {
		return companyService.DataSector(sector, startDate, endDate, type, range);
	}

		// companies and sector for grid
		@GetMapping("/grid/{startDate}/{endDate}")
			public List<DailyData> getGridData(@PathVariable("startDate") String startDate,
			@PathVariable("endDate") String endDate, @RequestParam(defaultValue = "") List<String> gotTickers,
			@RequestParam(defaultValue = "") List<String> gotSectors) throws ParseException {
			return companyService.getGridData(startDate, endDate, gotTickers, gotSectors);

		}

		// Companies selected return companies as list of object	
	
		@GetMapping("/chartCompanyObject/{tickerList}")
		public List<ChartObject> getChartCompany(@PathVariable("tickerList") List<String> tickerList,
			@RequestParam("type") String type) {
		return companyService.getChartCompany(tickerList, type);
		}
	
		// Sector selected return sectors as list of object	
	
		@GetMapping("/chartSectorObject/{sectorList}")
		public List<ChartObject> getChartSector(@PathVariable("sectorList") List<String> sectorList,
				@RequestParam("type") String type) {
			return companyService.getChartSector(sectorList, type);
		}
		
		//Companies and sectors matched, returns only companies list of  object 
		
		@GetMapping("/chartCompanySectorObject/{tickerList}/{sectorList}")
		public List<ChartObject> getChartCompanySector(@PathVariable("sectorList") List<String> sectorList,
				@PathVariable("tickerList") List<String> tickerList, @RequestParam("type") String type) {
			return companyService.getChartCompanySector(tickerList, sectorList, type);
		}
		
		// Companies and sectors matched, returns companies and sectors list of object

		@GetMapping("/chartAvgCompanySectorObject/{tickerList}/{sectorList}")
		public List<ChartObject> getAvgChartCompanySector(@PathVariable("sectorList") List<String> sectorList,
				@PathVariable("tickerList") List<String> tickerList, @RequestParam("type") String type) {
			return companyService.getAvgChartCompanySector(tickerList, sectorList, type);
		}
		
		////////    RETURNS DATE WISE DATA FOR COMPANIES ////////////
		@GetMapping("/chartCDCompany/{tickerList}/{startDate}/{endDate}")
		public ChartObjectCustom DailyCompanyObject(@PathVariable("tickerList") List<String> tickerList,
				@PathVariable("startDate") String startDate, @PathVariable("endDate") String endDate,
				@RequestParam("type") String type) throws ParseException {
			return companyService.DailyCompanyObject(tickerList, startDate, endDate, type);
		}
		
		
		////////RETURNS DATE WISE DATA FOR SECTORS ////////////
		@GetMapping("/chartCDSector/{sectorList}/{startDate}/{endDate}")
		public ChartObjectCustom DailySectorObject(@PathVariable("sectorList") List<String> sectorList,
				@PathVariable("startDate") String startDate, @PathVariable("endDate") String endDate,
				@RequestParam("type") String type) throws ParseException {
			return companyService.DailySectorObject(sectorList, startDate, endDate, type);
		}
		
		
		////////RETURNS DATE WISE DATA FOR MATCHED  COMPANY-SECTOR  ////////////
		@GetMapping("/chartCDCompanySector/{tickerList}/{sectorList}/{startDate}/{endDate}")
		public ChartObjectCustom DailyCompanySectorObject(@PathVariable("tickerList") List<String> tickerList,
				@PathVariable("sectorList") List<String> sectorList, @PathVariable("startDate") String startDate,
				@PathVariable("endDate") String endDate, @RequestParam("type") String type) throws ParseException {
			return companyService.DailyCompanySectorObject(tickerList, sectorList, startDate, endDate, type);
		}
		
		
		////////RETURNS DATE WISE DATA FOR MATCHED  COMPANY-SECTOR  ////////////
		@GetMapping("/chartCDAvgCompanySector/{tickerList}/{sectorList}/{startDate}/{endDate}")
		public ChartObjectCustom DailyAvgCompanySectorObject(@PathVariable("tickerList") List<String> tickerList,
				@PathVariable("sectorList") List<String> sectorList, @PathVariable("startDate") String startDate,
				@PathVariable("endDate") String endDate, @RequestParam("type") String type) throws ParseException {
			return companyService.DailyAvgCompanySectorObject(tickerList, sectorList, startDate, endDate, type);
		}
		
		
		////////RETURNS MONTH WISE DATA FOR COMPANIES ////////////
		@GetMapping("/chartCMCompany/{tickerList}/{startDate}/{endDate}")
		public ChartObjectCustom MonthlyCompanyObject(@PathVariable("tickerList") List<String> tickerList,
			@PathVariable("startDate") String startDate, @PathVariable("endDate") String endDate,
			@RequestParam("type") String type) throws ParseException {
		return companyService.MonthlyCompanyObject(tickerList, startDate, endDate, type);
		}
		
		
		////////RETURNS MONTH WISE DATA FOR SECTORS ////////////
		@GetMapping("/chartCMSector/{sectorList}/{startDate}/{endDate}")
		public ChartObjectCustom MonthlySectorObject(@PathVariable("sectorList") List<String> sectorList,
				@PathVariable("startDate") String startDate, @PathVariable("endDate") String endDate,
				@RequestParam("type") String type) throws ParseException {
			return companyService.MonthlySectorObject(sectorList, startDate, endDate, type);
		}
		
		
		
		////////RETURNS MONTH WISE DATA FOR MATCHED  COMPANY-SECTOR  ////////////
		@GetMapping("/chartCMCompanySector/{tickerList}/{sectorList}/{startDate}/{endDate}")
		public ChartObjectCustom MonthlyCompanySectorObject(@PathVariable("tickerList") List<String> tickerList,
				@PathVariable("sectorList") List<String> sectorList, @PathVariable("startDate") String startDate,
				@PathVariable("endDate") String endDate, @RequestParam("type") String type) throws ParseException {
			return companyService.MonthlyCompanySectorObject(tickerList, sectorList, startDate, endDate, type);
		}
		
		
		////////RETURNS DATE WISE DATA FOR MATCHED  COMPANY-SECTOR  ////////////
		@GetMapping("/chartCMAvgCompanySector/{tickerList}/{sectorList}/{startDate}/{endDate}")
		public ChartObjectCustom MonthlyAvgCompanySectorObject(@PathVariable("tickerList") List<String> tickerList,
				@PathVariable("sectorList") List<String> sectorList, @PathVariable("startDate") String startDate,
				@PathVariable("endDate") String endDate, @RequestParam("type") String type) throws ParseException {
			return companyService.MonthlyAvgCompanySectorObject(tickerList, sectorList, startDate, endDate, type);
		}
		
		
		
		
		
}

















