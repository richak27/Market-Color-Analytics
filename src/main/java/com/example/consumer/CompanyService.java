package com.example.consumer;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CompanyService {
	
	@Value("${token}")
    private String token;

	private static String url1 = "https://cloud.iexapis.com/stable/stock/";
	private static String url2_initial = "/chart/ytd?chartCloseOnly=true&token=";
	private static String url2_new = "/chart/ytd?chartLast=1&chartCloseOnly=true&token=";
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private CompanyRepository companyRepository;
	
	@Autowired
	private MongoTemplate mongoTemplate;	
	
	
	
	public Company getByTicker(String ticker) {
		return this.companyRepository.findByTicker(ticker);
	}
	
	public String addStocksByTicker(String ticker) {		
		Company company = this.companyRepository.findByTicker(ticker);
		Stock[] stocks = restTemplate.getForObject(url1+ticker+url2_initial+token, Stock[].class);
		company.setStocks(Arrays.asList(stocks));
		this.companyRepository.save(company);
		return ticker + "information added to DB";
	}	
	
	public Set<String> getAllTickers() {		
		return mongoTemplate.query(Company.class)  
				  .distinct("ticker")       
				  .as(String.class)           
				  .all()
				  .stream().collect(Collectors.toSet());
	}	
	
	public String seedDb() {
		
		Set<String> tickers = mongoTemplate.query(Company.class)  
				  				 .distinct("ticker")       
				  				 .as(String.class)           
				  				 .all()
				  				 .stream().collect(Collectors.toSet());
		for(String ticker : tickers) {
			try {
			Company company = this.companyRepository.findByTicker(ticker);
			Stock[] stocks = restTemplate.getForObject(url1+ticker+url2_initial+token, Stock[].class);
			company.setStocks(Arrays.asList(stocks));
			this.companyRepository.save(company);
			} catch(Exception exception) {
				System.out.println("Did not find " + ticker);
		}		
		}
		return "Seeding Successful!";
	}
	
	

}
