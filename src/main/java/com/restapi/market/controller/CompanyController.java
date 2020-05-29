package com.restapi.market.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.restapi.market.model.Company;
import com.restapi.market.model.Stock;
import com.restapi.market.service.CompanyService;

@RestController
@RequestMapping("/company")
public class CompanyController {
	@Autowired
	private CompanyService companyService;

	@GetMapping("/{ticker}")
	public Company getCompany(@PathVariable("ticker") String ticker) {
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
	public String updateByTicker(@PathVariable("ticker") String ticker) {
		return companyService.updateByTicker(ticker);
	}

	@GetMapping("/force-update")
	public void forceUpdate() {
		companyService.dailyUpdateAll();
	}
	
	@GetMapping("/getVolumeByCompany{ticker}")
	public float GetAvgVolumeByCompany(@PathVariable("ticker") String ticker)
	{
		Company company = companyService.getByTicker(ticker);
		return companyService.getAvgVolumeByCompany(company);
	} 

	@GetMapping("/getStockByCompany/{ticker}")
	public float GetAvgStockByCompany(@PathVariable("ticker") String ticker)
	{
		Company company = companyService.getByTicker(ticker);
		return companyService.getAvgStockByCompany(company);
	}
	
	
	@GetMapping("/getStockBySector/{sector}")
	public float getvalue(@PathVariable("sector") String sector)
	{
		List<Company> company = companyService.getBySector(sector);
		return companyService. getAvgStockBySector(company);
	}
}
