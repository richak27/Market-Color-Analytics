package com.restapi.market.controller;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.restapi.market.model.Company;
import com.restapi.market.model.PriceAverage;
import com.restapi.market.model.VolumeAverage;
import com.restapi.market.service.CompanyService;

@RestController
@RequestMapping("/company")
public class CompanyController {

	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

	@Autowired
	private CompanyService companyService;

	@GetMapping("/{ticker}")
	public Company getCompany(@PathVariable("ticker") String ticker) throws ParseException {
		return companyService.getByTicker(ticker);
	}

	@GetMapping("/tickers")
	public List<String> getTickerList() {
		return companyService.getAllTickers();
	}

	@GetMapping("sectors")
	public List<String> getSectorList() {
		return companyService.getAllSectors();
	}

	@GetMapping("/seed")
	public String populateDb() {
		return companyService.seedDb();
	}

	@GetMapping("/update/{ticker}")
	public String updateByTicker(@PathVariable("ticker") String ticker) throws ParseException {
		return companyService.updateByTicker(ticker);
	}

	@GetMapping("/force-update")	
	public void forceUpdate() {
		companyService.dailyUpdateAll();
	}

	@GetMapping("/add/{ticker}")
	public String addStocksByTicker(@PathVariable("ticker") String ticker) throws ParseException {
		return companyService.addStocksByTicker(ticker);
	}

	@GetMapping("/average-volume-company/{ticker}")
	@CrossOrigin(origins = "http://localhost:51535")
	public VolumeAverage calAverageVolume(@PathVariable("ticker") String ticker) {
		Company company = companyService.getByTicker(ticker);
		return companyService.calAvgVolByCompany(company);
	}

	@GetMapping("/average-price-company/{ticker}")
	@CrossOrigin(origins = "http://localhost:51535")
	public PriceAverage calAvgStockByCompany(@PathVariable("ticker") String ticker) {
		Company company = companyService.getByTicker(ticker);
		return companyService.calAvgPriceByCompany(company);
	}

	@GetMapping("/average-price-sector/{sector}")
	@CrossOrigin(origins = "http://localhost:51535")
	public PriceAverage calAvgStockBySector(@PathVariable("sector") String sector) {
		List<Company> company = companyService.getBySector(sector);
		return companyService.calAvgPriceBySector(company);
	}

	@GetMapping("/average-volume-sector/{sector}")
	@CrossOrigin(origins = "http://localhost:51535")
	public VolumeAverage calAvgVolumekBySector(@PathVariable("sector") String sector) {
		List<Company> company = companyService.getBySector(sector);
		return companyService.calAvgVolumeBySector(company);
	}

	@GetMapping("/volume-deviation-sector")
	@CrossOrigin(origins = "http://localhost:51535")
	public Map<String, Double> getSectorVolumeDeviation() {
		return companyService.getSectorVolumeDeviation();
	}

	@GetMapping("/price-deviation-sector")
	@CrossOrigin(origins = "http://localhost:51535")
	public Map<String, Double> getSectorPriceDeviation() {
		return companyService.getSectorPriceDeviation();
	}

	@GetMapping("/volume-deviation-company")
	@CrossOrigin(origins = "http://localhost:51535")
	public Map<String, Double> getCompanyVolumeDeviation() {
		return companyService.getCompanyVolumeDeviation();
	}

	@GetMapping("/price-deviation-company")
	@CrossOrigin(origins = "http://localhost:51535")
	public Map<String, Double> getCompanyPriceDeviation() {
		return companyService.getCompanyPriceDeviation();
	}
}
