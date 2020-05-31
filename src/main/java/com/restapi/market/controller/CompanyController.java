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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.restapi.market.model.Company;
import com.restapi.market.model.PriceAverage;
import com.restapi.market.model.VolumeAverage;
import com.restapi.market.service.CompanyService;
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@RequestMapping("/data")
public class CompanyController {

	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

	@Autowired
	private CompanyService companyService;

	@GetMapping("/{ticker}")
	@CrossOrigin(origins = "http://localhost:51535")
	public Company getCompany(@PathVariable("ticker") String ticker) throws ParseException {
		return companyService.getByTicker(ticker);
	}

	@GetMapping("/tickers")
	@CrossOrigin(origins = "http://localhost:51535")
	public List<String> getTickerList() {
		return companyService.getAllTickers();
	}
	
	@GetMapping("sectors")
	public List<String> getSectorList() {
		return companyService.getAllSectors();
	}

	@GetMapping("/seed")
	@CrossOrigin(origins = "http://localhost:51535")
	public String populateDb() {
		return companyService.seedDb();
	}

	@GetMapping("/update/{ticker}")
	@CrossOrigin(origins = "http://localhost:51535")
	public String updateByTicker(@PathVariable("ticker") String ticker) throws ParseException {
		return companyService.updateByTicker(ticker);
	}

	@GetMapping("/force-update")
	@CrossOrigin(origins = "http://localhost:51535")
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
		return companyService.calAvgVolByCompany(ticker);
	}
	
	@GetMapping("/average-price-company/{ticker}")
	@CrossOrigin(origins = "http://localhost:51535")	
	public PriceAverage calAvgPriceByCompany(@PathVariable("ticker") String ticker) {
		return companyService.calAvgPriceByCompany(ticker);
	}
	
	@GetMapping("/average-price-sector/{sector}")
	@CrossOrigin(origins = "http://localhost:51535")
	public PriceAverage calAvgStockBySector(@PathVariable("sector") String sector) {
		return companyService.calAvgPriceBySector(sector);
	}
	
	@GetMapping("/average-volume-sector/{sector}")
	@CrossOrigin(origins = "http://localhost:51535")
	public VolumeAverage calAvgVolumeBySector(@PathVariable("sector") String sector) {
		return companyService.calAvgVolumeBySector(sector);
	}
	

	@GetMapping("sort/company")
	@ResponseBody
	@CrossOrigin(origins = "http://localhost:51535")
	public Map<String, Double> getDeviationCompany(@RequestParam("rank") String rank) {
		return companyService.getDeviationCompany(rank);
	}
	
	@GetMapping("sort/sector")
	@ResponseBody
	@CrossOrigin(origins = "http://localhost:51535")
	public Map<String, Double> getDeviationSector(@RequestParam("rank") String rank) {
		return companyService.getDeviationSector(rank);
	}
	
}
