package com.restapi.market.controller;

import java.text.ParseException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
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
	public VolumeAverage calAverageVolume(@PathVariable("ticker") String ticker) {
		Company company = companyService.getByTicker(ticker);
		return companyService.calAvgVolByCompany(company);
	}
	
	@GetMapping("/average-stock-company/{ticker}")
	public PriceAverage calAvgStockByCompany(@PathVariable("ticker") String ticker) {
		Company company = companyService.getByTicker(ticker);
		return companyService.calAvgStockByCompany(company);
	}
	
	@GetMapping("/average-stock-sector/{sector}")
	public PriceAverage calAvgStockBySector(@PathVariable("sector") String sector) {
		List<Company> company = companyService.getBySector(sector);
		return companyService.calAvgStockBySector(company);
	}
	
	@GetMapping("/average-volume-sector/{sector}")
	public VolumeAverage calAvgVolumekBySector(@PathVariable("sector") String sector) {
		List<Company> company = companyService.getBySector(sector);
		return companyService.getAvgVolumekBySector(company);
	}
	
}
