package com.example.consumer;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CompanyService {
	
	@Value("${token}")
    private String token;

	private static String url1 = "https://sandbox.iexapis.com/stable/stock/";
	private static String url2 = "/chart/ytd?token=";
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private CompanyRepository companyRepository;
	
	public Company getByTicker(String ticker) {
		
	Company company = this.companyRepository.findByTicker(ticker);
    Stock[] stocks = restTemplate.getForObject(url1+ticker+url2+token, Stock[].class);
    company.setStocks(Arrays.asList(stocks));
    this.companyRepository.save(company);
    return company;
	}
	

}
