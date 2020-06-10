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

import com.restapi.market.model.ChartObjectCustom;
import com.restapi.market.model.Stock;

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

	// Populate Database
	@GetMapping("/seed")
	public String populateDb() {
		return companyService.seedDb();
	}

	// Sorted values of Deviation Price or Volume for a company
	@GetMapping("/sort/company")
	public Map<String, Double> getDeviationCompany(@RequestParam("rank") String rank,
			@RequestParam(defaultValue = "2020-02-09") String boundaryDate) throws ParseException {
		return companyService.getDeviationCompany(rank, boundaryDate);
	}

	// Sorted values of Deviation Price or Volume for a Sector
	@GetMapping("/sort/sector")
	public Map<String, Double> getDeviationSector(@RequestParam("rank") String rank,
			@RequestParam(defaultValue = "2020-02-09") String boundaryDate) throws ParseException {
		return companyService.getDeviationSector(rank, boundaryDate);
	}

	// Daily, Weekly, Monthly Average Stock Price/Volume for a company----------P
	@GetMapping("/company/{ticker}/{startDate}/{endDate}")
	public Map<String, Double> DataCompany(@PathVariable("ticker") String ticker,
			@PathVariable("startDate") String startDate, @PathVariable("endDate") String endDate,
			@RequestParam("type") String type, @RequestParam("range") String range) throws ParseException {
		return companyService.DataCompany(ticker, startDate, endDate, type, range);
	}

	// Daily, Weekly, Monthly Average Stock Price/Volume for a sector-----------P
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

	// Companies selected return companies as list of object------------D

	@GetMapping("/chartCompanyObject/{tickerList}")
	public List<ChartObject> getChartCompany(@PathVariable("tickerList") List<String> tickerList,
			@RequestParam("type") String type, @RequestParam(defaultValue = "2020-02-09") String boundaryDate) throws ParseException {
		return companyService.getChartCompany(tickerList, type, boundaryDate);
	}

	// Sector selected return sectors as list of object----------D

	@GetMapping("/chartSectorObject/{sectorList}")
	public List<ChartObject> getChartSector(@PathVariable("sectorList") List<String> sectorList,
			@RequestParam("type") String type, @RequestParam(defaultValue = "2020-02-09") String boundaryDate) throws ParseException {
		return companyService.getChartSector(sectorList, type, boundaryDate);
	}

	// Companies and sectors matched, returns only companies list of object--------D

	@GetMapping("/chartCompanySectorObject/{tickerList}/{sectorList}")
	public List<ChartObject> getChartCompanySector(@PathVariable("sectorList") List<String> sectorList,
			@PathVariable("tickerList") List<String> tickerList, @RequestParam("type") String type, @RequestParam(defaultValue = "2020-02-09") String boundaryDate) throws ParseException {
		return companyService.getChartCompanySector(tickerList, sectorList, type, boundaryDate);
	}

	// Companies and sectors matched, returns companies and sectors list of
	// object---------D

	@GetMapping("/chartAvgCompanySectorObject/{tickerList}/{sectorList}")
	public List<ChartObject> getAvgChartCompanySector(@PathVariable("sectorList") List<String> sectorList,
			@PathVariable("tickerList") List<String> tickerList, @RequestParam("type") String type, @RequestParam(defaultValue = "2020-02-09") String boundaryDate) throws ParseException {
		return companyService.getAvgChartCompanySector(tickerList, sectorList, type, boundaryDate);
	}

	//////// RETURNS DATE WISE DATA FOR COMPANIES ////////////Dh
	@GetMapping("/chartCDCompany/{tickerList}/{startDate}/{endDate}")
	public ChartObjectCustom DailyCompanyObject(@PathVariable("tickerList") List<String> tickerList,
			@PathVariable("startDate") String startDate, @PathVariable("endDate") String endDate,
			@RequestParam("type") String type) throws ParseException {
		return companyService.DailyCompanyObject(tickerList, startDate, endDate, type);
	}

	//////// RETURNS DATE WISE DATA FOR SECTORS ////////////Dh
	@GetMapping("/chartCDSector/{sectorList}/{startDate}/{endDate}")
	public ChartObjectCustom DailySectorObject(@PathVariable("sectorList") List<String> sectorList,
			@PathVariable("startDate") String startDate, @PathVariable("endDate") String endDate,
			@RequestParam("type") String type) throws ParseException {
		return companyService.DailySectorObject(sectorList, startDate, endDate, type);
	}

	//////// RETURNS DATE WISE DATA FOR MATCHED COMPANY ////////////Dh
	@GetMapping("/chartCDCompanySector/{tickerList}/{sectorList}/{startDate}/{endDate}")
	public ChartObjectCustom DailyCompanySectorObject(@PathVariable("tickerList") List<String> tickerList,
			@PathVariable("sectorList") List<String> sectorList, @PathVariable("startDate") String startDate,
			@PathVariable("endDate") String endDate, @RequestParam("type") String type) throws ParseException {
		return companyService.DailyCompanySectorObject(tickerList, sectorList, startDate, endDate, type);
	}

	//////// RETURNS DATE WISE DATA FOR MATCHED COMPANY-SECTOR ////////////Dh
	@GetMapping("/chartCDAvgCompanySector/{tickerList}/{sectorList}/{startDate}/{endDate}")
	public ChartObjectCustom DailyAvgCompanySectorObject(@PathVariable("tickerList") List<String> tickerList,
			@PathVariable("sectorList") List<String> sectorList, @PathVariable("startDate") String startDate,
			@PathVariable("endDate") String endDate, @RequestParam("type") String type) throws ParseException {
		return companyService.DailyAvgCompanySectorObject(tickerList, sectorList, startDate, endDate, type);
	}

	//////// RETURNS MONTH WISE DATA FOR COMPANIES ////////////Dh
	@GetMapping("/chartCMCompany/{tickerList}/{startDate}/{endDate}")
	public ChartObjectCustom MonthlyCompanyObject(@PathVariable("tickerList") List<String> tickerList,
			@PathVariable("startDate") String startDate, @PathVariable("endDate") String endDate,
			@RequestParam("type") String type) throws ParseException {
		return companyService.MonthlyCompanyObject(tickerList, startDate, endDate, type);
	}

	//////// RETURNS MONTH WISE DATA FOR SECTORS ////////////Dh
	@GetMapping("/chartCMSector/{sectorList}/{startDate}/{endDate}")
	public ChartObjectCustom MonthlySectorObject(@PathVariable("sectorList") List<String> sectorList,
			@PathVariable("startDate") String startDate, @PathVariable("endDate") String endDate,
			@RequestParam("type") String type) throws ParseException {
		return companyService.MonthlySectorObject(sectorList, startDate, endDate, type);
	}

	//////// RETURNS MONTH WISE DATA FOR MATCHED COMPANY ////////////Dh
	@GetMapping("/chartCMCompanySector/{tickerList}/{sectorList}/{startDate}/{endDate}")
	public ChartObjectCustom MonthlyCompanySectorObject(@PathVariable("tickerList") List<String> tickerList,
			@PathVariable("sectorList") List<String> sectorList, @PathVariable("startDate") String startDate,
			@PathVariable("endDate") String endDate, @RequestParam("type") String type) throws ParseException {
		return companyService.MonthlyCompanySectorObject(tickerList, sectorList, startDate, endDate, type);
	}

	//////// RETURNS MONTH WISE DATA FOR MATCHED COMPANY-SECTOR ////////////Dh
	@GetMapping("/chartCMAvgCompanySector/{tickerList}/{sectorList}/{startDate}/{endDate}")
	public ChartObjectCustom MonthlyAvgCompanySectorObject(@PathVariable("tickerList") List<String> tickerList,
			@PathVariable("sectorList") List<String> sectorList, @PathVariable("startDate") String startDate,
			@PathVariable("endDate") String endDate, @RequestParam("type") String type) throws ParseException {
		return companyService.MonthlyAvgCompanySectorObject(tickerList, sectorList, startDate, endDate, type);
	}

////////RETURNS WEEK WISE DATA FOR COMPANIES ////////////Dh
	@GetMapping("/chartCWCompany/{tickerList}/{startDate}/{endDate}")
	public ChartObjectCustom WeeklyCompanyObject(@PathVariable("tickerList") List<String> tickerList,
			@PathVariable("startDate") String startDate, @PathVariable("endDate") String endDate,
			@RequestParam("type") String type) throws ParseException {
		return companyService.WeeklyCompanyObject(tickerList, startDate, endDate, type);
	}

//////// RETURNS WEEK WISE DATA FOR SECTORS ////////////Dh
	@GetMapping("/chartCWSector/{sectorList}/{startDate}/{endDate}")
	public ChartObjectCustom WeeklySectorObject(@PathVariable("sectorList") List<String> sectorList,
			@PathVariable("startDate") String startDate, @PathVariable("endDate") String endDate,
			@RequestParam("type") String type) throws ParseException {
		return companyService.WeeklySectorObject(sectorList, startDate, endDate, type);
	}

//////// RETURNS WEEK WISE DATA FOR MATCHED COMPANY ////////////Dh
	@GetMapping("/chartCWCompanySector/{tickerList}/{sectorList}/{startDate}/{endDate}")
	public ChartObjectCustom WeeklyCompanySectorObject(@PathVariable("tickerList") List<String> tickerList,
			@PathVariable("sectorList") List<String> sectorList, @PathVariable("startDate") String startDate,
			@PathVariable("endDate") String endDate, @RequestParam("type") String type) throws ParseException {
		return companyService.WeeklyCompanySectorObject(tickerList, sectorList, startDate, endDate, type);
	}

//////// RETURNS WEEK WISE DATA FOR MATCHED COMPANY-SECTOR ////////////Dh
	@GetMapping("/chartCWAvgCompanySector/{tickerList}/{sectorList}/{startDate}/{endDate}")
	public ChartObjectCustom WeeklyAvgCompanySectorObject(@PathVariable("tickerList") List<String> tickerList,
			@PathVariable("sectorList") List<String> sectorList, @PathVariable("startDate") String startDate,
			@PathVariable("endDate") String endDate, @RequestParam("type") String type) throws ParseException {
		return companyService.WeeklyAvgCompanySectorObject(tickerList, sectorList, startDate, endDate, type);
	}

}