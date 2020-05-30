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
import com.restapi.market.model.Stock;
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

	@GetMapping("/average-volume/{ticker}")
	public VolumeAverage calAverageVolume(@PathVariable("ticker") String ticker) {
		Company company = companyService.getByTicker(ticker);
		return companyService.calAverageVolume(company);
	}

	@GetMapping("/average-stock/{ticker}")
	public PriceAverage calAverageStock(@PathVariable("ticker") String ticker) {
		Company company = companyService.getByTicker(ticker);
		return companyService.calAverageStock(company);
	}

}
