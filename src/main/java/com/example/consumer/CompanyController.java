package com.example.consumer;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


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
    public Set<String> getTickerList() {       
		return companyService.getAllTickers();        
    }
	
	@GetMapping("/seed")
    public String populateDb() {		
		return companyService.seedDb();
    }
	
	
	
	
		
	
}
