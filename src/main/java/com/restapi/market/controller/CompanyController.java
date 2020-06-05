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
import com.restapi.market.model.Company;
import com.restapi.market.model.DailyData;
import com.restapi.market.model.PriceAverage;
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

	// Calculate Pre and Post Covid price/volume for a company
	@GetMapping("/average-company/{ticker}")
	public AverageValues CompanyAverage(@PathVariable("ticker") String ticker, @RequestParam("type") String type) {
		return companyService.CompanyAverage(ticker, type);
	}

	// Calculate Pre and Post Covid price/volume for a company
	@GetMapping("/average-sector/{sector}")
	public AverageValues SectorAverage(@PathVariable("sector") String sector, @RequestParam("type") String type) {
		return companyService.SectorAverage(sector, type);
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

	// List of objects with daily data for a company
	@GetMapping("/gridCompany/{ticker}/{startDate}/{endDate}")
	public List<DailyData> gridCompany(@PathVariable("ticker") String ticker,
			@PathVariable("startDate") String startDate, @PathVariable("endDate") String endDate)
			throws ParseException {
		return companyService.gridCompany(ticker, startDate, endDate);
	}

	// List of objects with daily data for a sector
	@GetMapping("/gridSector/{sector}/{startDate}/{endDate}")
	public List<List<DailyData>> gridSector(@PathVariable("sector") String sector,
			@PathVariable("startDate") String startDate, @PathVariable("endDate") String endDate)
			throws ParseException {
		return companyService.gridSector(sector, startDate, endDate);
	}

	// Companies and Sectors Selected return only companies
	@GetMapping("/chartCompanySector/{tickerList}/{sectorList}")
	public Map<String, List<Double>> ChartCompanySector(@PathVariable("tickerList") List<String> tickerList,
			@PathVariable("sectorList") List<String> sectorList, @RequestParam("type") String type) {
		return companyService.ChartCompanySector(tickerList, sectorList, type);
	}

	// Companies and Sectors Selected return companies and avg sector
	@GetMapping("/chartCompanyAvgSector/{tickerList}/{sectorList}")
	public Map<String, List<Double>> AvgChartCompanySector(@PathVariable("tickerList") List<String> tickerList,
			@PathVariable("sectorList") List<String> sectorList, @RequestParam("type") String type) {
		return companyService.AvgChartCompanySector(tickerList, sectorList, type);
	}

	// Companies Selected return only companies

	@GetMapping("/chartCompany/{tickerList}")
	public Map<String, List<Double>> ChartCompany(@PathVariable("tickerList") List<String> tickerList,
			@RequestParam("type") String type) {
		return companyService.ChartCompany(tickerList, type);
	}

	// Sectors Selected return only Sectors

	@GetMapping("/chartSector/{sectorList}")
	public Map<String, List<Double>> ChartSector(@PathVariable("sectorList") List<String> sectorList,
			@RequestParam("type") String type) {
		return companyService.ChartSector(sectorList, type);
	}

}