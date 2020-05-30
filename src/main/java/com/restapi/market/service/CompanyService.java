package com.restapi.market.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.Comparator;
import static java.util.stream.Collectors.*;
import static java.util.Map.Entry.*;

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

	// daily update of stocks data for all companies
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

	// daily update of stocks data of company whose ticker is passed
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

	public List<Company> getBySector(String sector) {
		return this.companyRepository.findBySector(sector);
	}

	// seed database on company basis
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

	// get list of all tickers in database
	public List<String> getAllTickers() {
		return mongoTemplate.query(Company.class).distinct("ticker").as(String.class).all();
	}

	// get list of all tickers in database
	public List<String> getAllSectors() {
		return mongoTemplate.query(Company.class).distinct("sector").as(String.class).all();
	}

	// seed database with data of all companies
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

	public VolumeAverage calAvgVolByCompany(Company company) {
		VolumeAverage volumeAverage = new VolumeAverage();
		double sum_volume_pre = 0;
		double sum_volume_post = 0;
		int sizeofpre = 0;
		List<Stock> stocks = company.getStocks();
		for (Stock stock : stocks) {
			if (stock.getPeriod().contentEquals("pre")) {
				sizeofpre = sizeofpre + 1;
				sum_volume_pre += stock.getVolume();
			} else {
				sum_volume_post += stock.getVolume();
			}
		}

		volumeAverage.setPreCovidVolume((sum_volume_pre) / (sizeofpre));
		volumeAverage.setPostCovidVolume((sum_volume_post) / (stocks.size() - sizeofpre));
		volumeAverage.setDeviationVolume(volumeAverage.getPostCovidVolume() - volumeAverage.getPreCovidVolume());

		return volumeAverage;

	}

	public PriceAverage calAvgStockByCompany(Company company) {
		PriceAverage priceAverage = new PriceAverage();
		double sum_close_pre = 0;
		double sum_close_post = 0;
		int sizeofpre = 0; // Size of pre covid stocks

		List<Stock> stocks = company.getStocks();

		for (Stock stock : stocks) {

			if (stock.getPeriod().contentEquals("pre")) {

				sum_close_pre += stock.getClose();
				sizeofpre = sizeofpre + 1;
			}

			else {
				sum_close_post += stock.getClose();
			}
		}

		priceAverage.setPreCovidPrice((sum_close_pre) / (sizeofpre));
		priceAverage.setPostCovidPrice((sum_close_post) / (stocks.size() - sizeofpre));
		priceAverage.setDeviationPrice(priceAverage.getPostCovidPrice() - priceAverage.getPreCovidPrice());

		return priceAverage;

	}

	public PriceAverage calAvgStockBySector(List<Company> company) {
		PriceAverage priceAverage = new PriceAverage();
		double pre_sum_stock = 0, post_sum_stock = 0;

		for (Company comp : company) {

			pre_sum_stock = pre_sum_stock + calAvgStockByCompany(comp).getPreCovidPrice();
			post_sum_stock = post_sum_stock + calAvgStockByCompany(comp).getPostCovidPrice();

		}

		priceAverage.setPreCovidPrice((pre_sum_stock) / (company.size()));
		priceAverage.setPostCovidPrice((post_sum_stock) / (company.size()));
		priceAverage.setDeviationPrice(priceAverage.getPostCovidPrice() - priceAverage.getPreCovidPrice());

		return priceAverage;

	}

	public VolumeAverage calAvgVolumeBySector(List<Company> company) {
		VolumeAverage volumeAverage = new VolumeAverage();
		double pre_sum_volume = 0, post_sum_volume = 0;

		for (Company comp : company) {

			pre_sum_volume = pre_sum_volume + calAvgVolByCompany(comp).getPreCovidVolume();
			post_sum_volume = post_sum_volume + calAvgStockByCompany(comp).getPostCovidPrice();

		}

		volumeAverage.setPreCovidVolume((pre_sum_volume) / (company.size()));
		volumeAverage.setPostCovidVolume((post_sum_volume) / (company.size()));
		volumeAverage.setDeviationVolume(volumeAverage.getPostCovidVolume() - volumeAverage.getPreCovidVolume());

		return volumeAverage;

	}

	// Sort Functions for Sector-wise Deviation:

	// Sort Average Volume Deviation of Sectors
	public Map<String, Double> getSectorVolumeDeviation() {
		List<String> SectorList = getAllSectors();
		Map<String, Double> Values = new HashMap<String, Double>();

		for (String i : SectorList) {
			List<Company> company = getBySector(i);
			VolumeAverage volumeAverage = calAvgVolumeBySector(company);
			Values.put(i, volumeAverage.getDeviationVolume());
		}
		Map<String, Double> SortedValues = Values.entrySet().stream().sorted(comparingByValue())
				.collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));

		return SortedValues;
	}

	// Sort Average Price Deviation of Sectors
	public Map<String, Double> getSectorPriceDeviation() {
		List<String> SectorList = getAllSectors();
		Map<String, Double> Values = new HashMap<String, Double>();

		for (String i : SectorList) {

			List<Company> company = getBySector(i);
			PriceAverage priceAverage = calAvgStockBySector(company);
			Values.put(i, priceAverage.getDeviationPrice());
		}
		Map<String, Double> SortedValues = Values.entrySet().stream().sorted(comparingByValue())
				.collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));

		return SortedValues;
	}

	// Sort Functions for Company-wise Deviation:

	// Sort Average Volume Deviation of Company
	public Map<String, Double> getCompanyVolumeDeviation() {
		List<String> TickerList = getAllTickers();
		Map<String, Double> Values = new HashMap<String, Double>();

		for (String i : TickerList) {
			Company company = getByTicker(i);
			VolumeAverage volumeAverage = calAvgVolByCompany(company);
			Values.put(i, volumeAverage.getDeviationVolume());
		}
		Map<String, Double> SortedValues = Values.entrySet().stream().sorted(comparingByValue())
				.collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));

		return SortedValues;
	}

	// Sort Average Price Deviation of Company
	public Map<String, Double> getCompanyPriceDeviation() {
		List<String> TickerList = getAllTickers();
		Map<String, Double> Values = new HashMap<String, Double>();

		for (String i : TickerList) {
			Company company = getByTicker(i);
			PriceAverage priceAverage = calAvgStockByCompany(company);
			Values.put(i, priceAverage.getDeviationPrice());
		}

		Map<String, Double> SortedValues = Values.entrySet().stream().sorted(comparingByValue())
				.collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));

		return SortedValues;
	}
}
