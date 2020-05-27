package com.example.consumer;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CompanyService {
	
	@Value("${token}")
    private String token;

	private static String url1 = "https://sandbox.iexapis.com/stable/stock/";
	private static String url2 = "/chart/ytd?token=";
	public List<Company>obj;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private CompanyRepository companyRepository;
	
	private MongoTemplate mongoTemplate;
	
	public CompanyService(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}
	
	public Company getByTicker(String ticker) {
		
		Company company = this.companyRepository.findByTicker(ticker);
	    Stock[] stocks = restTemplate.getForObject(url1+ticker+url2+token, Stock[].class);
	    company.setStocks(Arrays.asList(stocks));
	    this.companyRepository.save(company);
	    return company;
		}
	
	
	public List<String>getAllTickers(){
		return mongoTemplate.query(Company.class)
				.distinct("ticker")
				.as(String.class)
				.all();
	}
	
	public List<String>getAllSectors(){
		return mongoTemplate.query(Company.class)
				.distinct("sector")
				.as(String.class)
				.all();
	}
		
	public List<Company> getCompanyFromTicker(List<String>list) {		
		for(int i = 0; i < list.size(); i++) {
			System.out.println(i);
			Company company = getByTicker(list.get(i));
			obj.add(i, company);
		}
		return obj;				
		}
	
	
}
