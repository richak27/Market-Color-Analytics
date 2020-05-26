package com.example.consumer;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/company")
public class DbSeeder {
	@Value("${token}")
    private String token;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private CompanyRepository companyRepository;
	
	
	private static String url1 = "https://sandbox.iexapis.com/stable/stock/";
	private static String url2 = "/chart/ytd?token=";

	@GetMapping("/{ticker}")
    public Company getByTicker(@PathVariable("ticker") String ticker){
        Company company = this.companyRepository.findByTicker(ticker);
        Stock[] stocks = restTemplate.getForObject(url1+ticker+url2+token, Stock[].class);
        company.setStocks(Arrays.asList(stocks));
        this.companyRepository.save(company);
        return company;
    }
	
	
}
