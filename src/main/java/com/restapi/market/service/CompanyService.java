package com.restapi.market.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
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
	@Value("${boundary.date}")
	private String boundaryDate;

	private static String url1 = "https://cloud.iexapis.com/stable/stock/";
	private static String url2_initial = "/chart/ytd?chartCloseOnly=true&token=";
	private static String url2_new = "/chart/ytd?chartLast=1&chartCloseOnly=true&token=";
	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private CompanyRepository companyRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	SimpleDateFormat converter = new SimpleDateFormat("yyyy-MM-dd");

	public void dailyUpdateAll() {
		List<String> tickers = mongoTemplate.query(Company.class).distinct("ticker").as(String.class).all();
		for (String ticker : tickers) {
			try {
				Stock[] stocks = restTemplate.getForObject(url1 + ticker + url2_new + token, Stock[].class);
				for (Stock stock : stocks) {
					String sDate = stock.getDate();
					Date nowDate = converter.parse(sDate);
					Date thresholdDate = converter.parse(boundaryDate);
					if (nowDate.before(thresholdDate) || nowDate.equals(thresholdDate)) {
						stock.setPeriod("pre");
					} else {
						stock.setPeriod("post");
					}
				}
				mongoTemplate.updateFirst(new Query(Criteria.where("ticker").is(ticker)),
						new Update().addToSet("stocks", stocks[0]), Company.class);
			} catch (Exception exception) {
				System.out.println(exception);
			}
		}
	}
	
	
	public String updateByTicker(String ticker) throws ParseException {
		Stock[] stocks = restTemplate.getForObject(url1 + ticker + url2_new + token, Stock[].class);
		for (Stock stock : stocks) {
			String sDate = stock.getDate();
			Date nowDate = converter.parse(sDate);
			Date thresholdDate = converter.parse(boundaryDate);
			if (nowDate.before(thresholdDate) || nowDate.equals(thresholdDate)) {
				stock.setPeriod("pre");
			} else {
				stock.setPeriod("post");
			}
		}
		mongoTemplate.updateFirst(new Query(Criteria.where("ticker").is(ticker)),
				new Update().addToSet("stocks", stocks[0]), Company.class);
		return "Stocks data updated successfully!";
	}
	

	public Company getByTicker(String ticker) {
		return this.companyRepository.findByTicker(ticker);
	}
	

	public String addStocksByTicker(String ticker) throws ParseException {
		Company company = this.companyRepository.findByTicker(ticker);
		Stock[] stocks = restTemplate.getForObject(url1 + ticker + url2_initial + token, Stock[].class);
		for (Stock stock : stocks) {
			String sDate = stock.getDate();
			Date nowDate = converter.parse(sDate);
			Date thresholdDate = converter.parse(boundaryDate);
			if (nowDate.before(thresholdDate) || nowDate.equals(thresholdDate)) {
				stock.setPeriod("pre");
			} else {
				stock.setPeriod("post");
			}
		}
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
				for (Stock stock : stocks) {
					String sDate = stock.getDate();
					Date nowDate = converter.parse(sDate);
					Date thresholdDate = converter.parse(boundaryDate);
					if (nowDate.before(thresholdDate) || nowDate.equals(thresholdDate)) {
						stock.setPeriod("pre");
					} else {
						stock.setPeriod("post");
					}
				}
				company.setStocks(Arrays.asList(stocks));
				this.companyRepository.save(company);
			} catch (Exception exception) {
				System.out.println("Did not find " + ticker);
			}
		}
		return "Seeding Successful!";
	}

}
