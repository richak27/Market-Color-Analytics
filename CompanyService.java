package com.restapi.market.service;

import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.restapi.market.model.Company;
import com.restapi.market.model.Stock;
import com.restapi.market.repository.CompanyRepository;

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

	public void dailyUpdateAll() {
		List<String> tickers = mongoTemplate.query(Company.class).distinct("ticker").as(String.class).all();
		for (String ticker : tickers) {
			try {
				Stock[] stocks = restTemplate.getForObject(url1 + ticker + url2_new + token, Stock[].class);
				mongoTemplate.updateFirst(new Query(Criteria.where("ticker").is(ticker)),
						new Update().addToSet("stocks", stocks[0]), Company.class);
			} catch (Exception exception) {
				System.out.println(exception);
			}
		}
	}

	public String updateByTicker(String ticker) {
		Stock[] stocks = restTemplate.getForObject(url1 + ticker + url2_new + token, Stock[].class);
		mongoTemplate.updateFirst(new Query(Criteria.where("ticker").is(ticker)),
				new Update().addToSet("stocks", stocks[0]), Company.class);
		return "Stocks data updated successfully!";
	}

	public Company getByTicker(String ticker) {
		return this.companyRepository.findByTicker(ticker);
	}

	public String addStocksByTicker(String ticker) {
		Company company = this.companyRepository.findByTicker(ticker);
		Stock[] stocks = restTemplate.getForObject(url1 + ticker + url2_initial + token, Stock[].class);
		company.setStocks(Arrays.asList(stocks));
		this.companyRepository.save(company);
		return ticker + "information added to DB";
	}

	public List<String> getAllTickers() {
		return mongoTemplate.query(Company.class).distinct("ticker").as(String.class).all();
	}

	public String seedDb() {
		List<String> tickers = mongoTemplate.query(Company.class).distinct("ticker").as(String.class).all();
		for (String ticker : tickers) {
			try {
				Company company = this.companyRepository.findByTicker(ticker);
				Stock[] stocks = restTemplate.getForObject(url1 + ticker + url2_initial + token, Stock[].class);
				company.setStocks(Arrays.asList(stocks));
				this.companyRepository.save(company);
			} catch (Exception exception) {
				System.out.println("Did not find " + ticker);
			}
		}
		return "Seeding Successful!";
	}
	
	
	public float calAverageVol(Company company)
	{
		float sum_vol = 0;
		List<Stock> stocks = company.getStocks();
		for (Stock stock: stocks) {
			sum_vol = sum_vol + stock.getVolume();	
		}
		return (sum_vol/stocks.size());
	}
	
	public float calAverageStock(Company company)
	{
		float sum_close = 0;
		List<Stock> stocks = company.getStocks();
		for (Stock stock: stocks) {
			sum_close = sum_close + stock.getClose();	
		}
		return (sum_close/stocks.size());
	}

}
