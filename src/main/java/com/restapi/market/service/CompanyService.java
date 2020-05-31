package com.restapi.market.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

	// returns company object when ticker is passed
	public Company getByTicker(String ticker) {
		return this.companyRepository.findByTicker(ticker);
	}

	// returns list of company objects belonging to a given sector
	public List<Company> getBySector(String sector) {
		return this.companyRepository.findBySector(sector);
	}

	// get list of all tickers in database
	public List<String> getAllTickers() {
		return mongoTemplate.query(Company.class).distinct("ticker").as(String.class).all();
	}

	// get list of all sectors in database
	public List<String> getAllSectors() {
		return mongoTemplate.query(Company.class).distinct("sector").as(String.class).all();
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

	// seed database with data of all companies
	public String seedDb() {
		List<String> tickers = mongoTemplate.query(Company.class).distinct("ticker").as(String.class).all();
		for (String ticker : tickers) {
			try {
				addStocksByTicker(ticker);
			} catch (Exception exception) {
				System.out.println("Did not find " + ticker);
			}
		}
		return "Seeding Successful!";
	}

	// daily update of stocks data of company whose ticker is passed
	public void updateByTicker(String ticker) throws ParseException {
		Stock[] stocks = restTemplate.getForObject(url1 + ticker + url2_new + token, Stock[].class); // returns only one
																										// object
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
	}

	// daily update of stocks data for all companies
	public void dailyUpdateAll() {
		List<String> tickers = mongoTemplate.query(Company.class).distinct("ticker").as(String.class).all();
		for (String ticker : tickers) {
			try {
				updateByTicker(ticker);
			} catch (Exception exception) {
				System.out.println(exception);
			}
		}
	}

	// calculate average volume for a company by ticker
	public VolumeAverage calAvgVolByCompany(String ticker) {
		Company company = getByTicker(ticker);
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

	// calculate average stock-price for a company by ticker
	public PriceAverage calAvgPriceByCompany(String ticker) {
		Company company = getByTicker(ticker);
		PriceAverage priceAverage = new PriceAverage();
		double sum_close_pre = 0;
		double sum_close_post = 0;
		int sizeofpre = 0;

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

	// calculate average stock-price for a sector
	public PriceAverage calAvgPriceBySector(String sector) {
		List<Company> company = getBySector(sector);
		PriceAverage priceAverage = new PriceAverage();
		double pre_sum_price = 0, post_sum_price = 0;

		for (Company comp : company) {

			pre_sum_price = pre_sum_price + calAvgPriceByCompany(comp.getTicker()).getPreCovidPrice();

			post_sum_price = post_sum_price + calAvgPriceByCompany(comp.getTicker()).getPostCovidPrice();

		}

		priceAverage.setPreCovidPrice((pre_sum_price) / (company.size()));
		priceAverage.setPostCovidPrice((post_sum_price) / (company.size()));
		priceAverage.setDeviationPrice(priceAverage.getPostCovidPrice() - priceAverage.getPreCovidPrice());

		return priceAverage;

	}

	// calculate average volume for a sector
	public VolumeAverage calAvgVolumeBySector(String sector) {
		List<Company> company = getBySector(sector);

		VolumeAverage volumeAverage = new VolumeAverage();
		double pre_sum_volume = 0, post_sum_volume = 0;

		for (Company comp : company) {
			pre_sum_volume = pre_sum_volume + calAvgVolByCompany(comp.getTicker()).getPreCovidVolume();

			post_sum_volume = post_sum_volume + calAvgPriceByCompany(comp.getTicker()).getPostCovidPrice();

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
			VolumeAverage volumeAverage = calAvgVolumeBySector(i);
			Values.put(i, volumeAverage.getDeviationVolume());
		}
		Map<String, Double> SortedValues = Values.entrySet().stream().sorted(comparingByValue())
				.collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
		return SortedValues;
	}

	// Sort Average stock-price Deviation of Sectors
	public Map<String, Double> getSectorPriceDeviation() {
		List<String> SectorList = getAllSectors();
		Map<String, Double> Values = new HashMap<String, Double>();
		for (String i : SectorList) {

			PriceAverage priceAverage = calAvgPriceBySector(i);
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
			VolumeAverage volumeAverage = calAvgVolByCompany(i);
			Values.put(i, volumeAverage.getDeviationVolume());
		}
		Map<String, Double> SortedValues = Values.entrySet().stream().sorted(comparingByValue())
				.collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
		return SortedValues;
	}

	// Sort Average stock-price Deviation of Company
	public Map<String, Double> getCompanyPriceDeviation() {
		List<String> TickerList = getAllTickers();
		Map<String, Double> Values = new HashMap<String, Double>();

		for (String i : TickerList) {
			PriceAverage priceAverage = calAvgPriceByCompany(i);
			Values.put(i, priceAverage.getDeviationPrice());
		}

		Map<String, Double> SortedValues = Values.entrySet().stream().sorted(comparingByValue())
				.collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
		return SortedValues;
	}

	// Sorted Deviation for Companies
	public Map<String, Double> getDeviationCompany(String rank) {

		if (rank.contentEquals("volume")) {
			return getCompanyVolumeDeviation();
		}

		else {
			return getCompanyPriceDeviation();
		}

	}

	// Sorted Deviation for Sectors
	public Map<String, Double> getDeviationSector(String rank) {
		if (rank.contentEquals("volume")) {
			return getSectorVolumeDeviation();
		}

		else {
			return getSectorPriceDeviation();
		}

	}

}
