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
import com.restapi.market.model.PriceAverage;
import com.restapi.market.model.Stock;
import com.restapi.market.model.VolumeAverage;
import com.restapi.market.repository.CompanyRepository;

@Service
public class CompanyService {

	@Value("${token}")
	private String token;

	@Value("${boundary.date}")
	private String boundaryDate;

	private static String url1 = "https://sandbox.iexapis.com/stable/stock/";
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


	public VolumeAverage calAverageVolume(Company company)
	{
		VolumeAverage volumeAverage = new VolumeAverage();
		double sum_volume_pre = 0;
		double sum_volume_post = 0;
		int sizeofpre = 0;
		List<Stock> stocks = company.getStocks();
		for (Stock stock: stocks) {
			if(stock.getPeriod().contentEquals("pre")) {
				sizeofpre = sizeofpre+1;
				sum_volume_pre += stock.getVolume();	
			}
			else {
				sum_volume_post += stock.getVolume();
			}
		}

		volumeAverage.setPreCovidVolume((sum_volume_pre)/(sizeofpre));
		volumeAverage.setPostCovidVolume((sum_volume_post) /(stocks.size()-sizeofpre));
		volumeAverage.setDeviationVolume(volumeAverage.getPostCovidVolume()-volumeAverage.getPreCovidVolume());

		return volumeAverage;

	}

	public PriceAverage calAverageStock(Company company)
	{
		PriceAverage priceAverage = new PriceAverage();
		double sum_close_pre = 0;
		double sum_close_post = 0;
		int  sizeofpre = 0; // Size of pre covid stocks
		
		List<Stock> stocks = company.getStocks();
		
		for (Stock stock: stocks) {
		
			if(stock.getPeriod().contentEquals("pre")) {
				
				sum_close_pre += stock.getClose();
				sizeofpre = sizeofpre+1;}

			else {
				sum_close_post +=stock.getClose();			}
		}
		
		priceAverage.setPreCovidPrice((sum_close_pre)/(sizeofpre));
		priceAverage.setPostCovidPrice((sum_close_post) /(stocks.size()-sizeofpre));
		priceAverage.setDeviationPrice(priceAverage.getPostCovidPrice()-priceAverage.getPreCovidPrice());
		

		return priceAverage;

	}


}
