package com.restapi.market.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

import com.restapi.market.model.AverageValues;
import com.restapi.market.model.Calculate;
import com.restapi.market.model.ChartObject;
import com.restapi.market.model.ChartObjectCustom;
import com.restapi.market.model.Company;
import com.restapi.market.model.DailyData;
import com.restapi.market.model.Stock;
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

	Calendar cal = Calendar.getInstance();

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
			Date nowDate = converter.parse(stock.getDate());
			Date thresholdDate = converter.parse(boundaryDate);
			cal.setTime(nowDate);
			int week = cal.get(Calendar.WEEK_OF_YEAR);
			stock.setWeek(week);
			stock.setMonth(stock.getDate().substring(5, 7));

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

			Date nowDate = converter.parse(stock.getDate());
			cal.setTime(nowDate);
			int week = cal.get(Calendar.WEEK_OF_YEAR);
			stock.setWeek(week);
			stock.setMonth(stock.getDate().substring(5, 7));

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
	public AverageValues calAvgVolumeByCompany(String ticker) {
		Company company = getByTicker(ticker);
		AverageValues volumeAverage = new AverageValues();
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

		volumeAverage.setPreCovidValue((sum_volume_pre) / (sizeofpre));
		volumeAverage.setPostCovidValue((sum_volume_post) / (stocks.size() - sizeofpre));
		volumeAverage.setDeviation(volumeAverage.getPostCovidValue() - volumeAverage.getPreCovidValue());

		return volumeAverage;

	}

	// calculate average stock-price for a company by ticker
	public AverageValues calAvgPriceByCompany(String ticker) {
		Company company = getByTicker(ticker);
		AverageValues priceAverage = new AverageValues();
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

		priceAverage.setPreCovidValue((sum_close_pre) / (sizeofpre));
		priceAverage.setPostCovidValue((sum_close_post) / (stocks.size() - sizeofpre));
		priceAverage.setDeviation(priceAverage.getPostCovidValue() - priceAverage.getPreCovidValue());

		return priceAverage;

	}

	// calculate average stock-price for a sector
	public AverageValues calAvgPriceBySector(String sector) {
		List<Company> company = getBySector(sector);
		AverageValues priceAverage = new AverageValues();
		double pre_sum_price = 0, post_sum_price = 0;

		for (Company comp : company) {

			pre_sum_price = pre_sum_price + calAvgPriceByCompany(comp.getTicker()).getPreCovidValue();

			post_sum_price = post_sum_price + calAvgPriceByCompany(comp.getTicker()).getPostCovidValue();

		}

		priceAverage.setPreCovidValue((pre_sum_price) / (company.size()));
		priceAverage.setPostCovidValue((post_sum_price) / (company.size()));
		priceAverage.setDeviation(priceAverage.getPostCovidValue() - priceAverage.getPreCovidValue());

		return priceAverage;

	}

	// calculate average volume for a sector
	public AverageValues calAvgVolumeBySector(String sector) {
		List<Company> company = getBySector(sector);

		AverageValues volumeAverage = new AverageValues();
		double pre_sum_volume = 0, post_sum_volume = 0;

		for (Company comp : company) {
			pre_sum_volume = pre_sum_volume + calAvgVolumeByCompany(comp.getTicker()).getPreCovidValue();

			post_sum_volume = post_sum_volume + calAvgPriceByCompany(comp.getTicker()).getPostCovidValue();

		}

		volumeAverage.setPreCovidValue((pre_sum_volume) / (company.size()));
		volumeAverage.setPostCovidValue((post_sum_volume) / (company.size()));
		volumeAverage.setDeviation(volumeAverage.getPostCovidValue() - volumeAverage.getPreCovidValue());

		return volumeAverage;

	}

	// Calculate average values for a company
	public AverageValues CompanyAverage(String ticker, String type) {

		if (type.contentEquals("price")) {

			return calAvgPriceByCompany(ticker);
		}

		else if (type.contentEquals("volume")) {

			return calAvgVolumeByCompany(ticker);
		}

		else {
			return null;
		}
	}

	// Calculate average values for a sector
	public AverageValues SectorAverage(String sector, String type) {

		if (type.contentEquals("price")) {

			return calAvgPriceBySector(sector);
		}

		else if (type.contentEquals("volume")) {

			return calAvgVolumeBySector(sector);
		}

		else {
			return null;
		}
	}

	// Sort Functions for Sector-wise Deviation:

	// Sort Average Volume Deviation of Sectors
	public Map<String, Double> getSectorVolumeDeviation() {
		List<String> SectorList = getAllSectors();
		Map<String, Double> Values = new HashMap<String, Double>();
		for (String i : SectorList) {
			AverageValues volumeAverage = calAvgVolumeBySector(i);
			Values.put(i, volumeAverage.getDeviation());
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

			AverageValues priceAverage = calAvgPriceBySector(i);
			Values.put(i, priceAverage.getDeviation());
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
			AverageValues volumeAverage = calAvgVolumeByCompany(i);
			Values.put(i, volumeAverage.getDeviation());
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
			AverageValues priceAverage = calAvgPriceByCompany(i);
			Values.put(i, priceAverage.getDeviation());
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

		else if (rank.contentEquals("price")) {
			return getSectorPriceDeviation();
		} else {
			return null;
		}

	}

	// Calculate Average Stock Price and Volume
	public Calculate averagestock(List<Stock> stocks) {

		Calculate cal = new Calculate();
		double sum_close = 0;
		double sum_volume = 0;
		for (Stock stock : stocks) {
			sum_close += stock.getClose();
			sum_volume += stock.getVolume();
		}
		cal.setPrice(sum_close / stocks.size());
		cal.setVolume(sum_volume / stocks.size());
		return cal;
	}

	// Calculate for Average date range by company public Calculate----summary line
	public Calculate getDataByRangeCompany(String ticker, String startDate, String endDate) throws ParseException {
		Company company = getByTicker(ticker);
		List<Stock> stocks = company.getStocks();
		List<Stock> stocksnew = new ArrayList<>();
		Date eDate = converter.parse(endDate);
		Date sDate = converter.parse(startDate);
		for (Stock stock : stocks) {

			String nDate = stock.getDate();
			Date nowDate = converter.parse(nDate);
			if (nowDate.before(eDate) && nowDate.after(sDate) || nowDate.equals(sDate) || nowDate.equals(eDate)) {
				stocksnew.add(stock);
			}
		}
		return averagestock(stocksnew);
	}

	// Calculate for Average date range by sector public Calculate--------summary
	// line
	public Calculate getDataByRangeSector(String sector, String startDate, String endDate) throws ParseException {
		List<Company> companies = getBySector(sector);
		List<Stock> stocksnew = new ArrayList<>();
		Date eDate = converter.parse(endDate);
		Date sDate = converter.parse(startDate);
		for (Company comp : companies) {

			List<Stock> stocks = comp.getStocks();
			for (Stock stock : stocks) {

				String nDate = stock.getDate();
				Date nowDate = converter.parse(nDate);
				if (nowDate.before(eDate) && nowDate.after(sDate) || nowDate.equals(sDate) || nowDate.equals(eDate)) {
					stocksnew.add(stock);
				}
			}
		}
		return averagestock(stocksnew);
	}

/////////////                              DAILY COMPANY                          ////////////-----p
	public Map<String, Double> DailyCompany(String ticker, String frdate, String todate, String type)
			throws ParseException {

		Date toDate = converter.parse(todate);
		Date frDate = converter.parse(frdate);
		Company company = getByTicker(ticker);
		List<Stock> stocknew = new ArrayList<>();

		List<Stock> stocks = company.getStocks();
		for (Stock stock : stocks) {

			Date nDate = converter.parse(stock.getDate());
			if (nDate.before(toDate) && nDate.after(frDate) || nDate.equals(toDate) || nDate.equals(frDate)) {
				stocknew.add(stock);
			}
		}

		if (type.contentEquals("price")) {
			Map<String, Double> value = stocknew.stream()
					.collect(Collectors.groupingBy(Stock::getDate, Collectors.averagingDouble(Stock::getClose)));

			Map<String, Double> daily = value.entrySet().stream().sorted(comparingByKey())
					.collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
			return daily;
		}

		else if (type.contentEquals("volume")) {
			Map<String, Double> value = stocknew.stream()
					.collect(Collectors.groupingBy(Stock::getDate, Collectors.averagingDouble(Stock::getVolume)));

			Map<String, Double> daily = value.entrySet().stream().sorted(comparingByKey())
					.collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
			return daily;

		}

		else {

			System.out.print("Enter correct parameters");
			return null;
		}
	}

/////////////////                     DAILY SECTOR                     /////////////////----p
	public Map<String, Double> DailySector(String sector, String startdate, String enddate, String type)
			throws ParseException {

		List<Company> company = getBySector(sector);
		Date startDate = converter.parse(startdate);
		Date endDate = converter.parse(enddate);
		List<Stock> stocknew = new ArrayList<>();
		for (Company comp : company) {

			List<Stock> stocks = comp.getStocks();
			for (Stock stock : stocks) {

				Date nDate = converter.parse(stock.getDate());
				if (nDate.before(endDate) && nDate.after(startDate) || nDate.equals(startDate)
						|| nDate.equals(endDate)) {
					stocknew.add(stock);
				}
			}

		}

		if (type.contentEquals("price")) {
			Map<String, Double> value = stocknew.stream()
					.collect(Collectors.groupingBy(Stock::getDate, Collectors.averagingDouble(Stock::getClose)));

			Map<String, Double> daily = value.entrySet().stream().sorted(comparingByKey())
					.collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
			return daily;
		}

		else if (type.contentEquals("volume")) {
			Map<String, Double> value = stocknew.stream()
					.collect(Collectors.groupingBy(Stock::getDate, Collectors.averagingDouble(Stock::getVolume)));

			Map<String, Double> daily = value.entrySet().stream().sorted(comparingByKey())
					.collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
			return daily;

		} else {

			System.out.print("Enter correct parameters");
			return null;
		}

	}

//////////////                      WEEKLY COMPANY              //////////////////////---p
	public Map<Integer, Double> WeeklyCompany(String ticker, String startDate, String endDate, String type)
			throws ParseException {
		Company company = getByTicker(ticker);
		List<Stock> stocks = company.getStocks();
		List<Stock> stocksnew = new ArrayList<>();
		Date sDate = converter.parse(startDate);
		Date eDate = converter.parse(endDate);
		for (Stock stock : stocks) {
			Date nowDate = converter.parse(stock.getDate());
			if ((nowDate.after(sDate) && nowDate.before(eDate)) || nowDate.equals(sDate) || nowDate.equals(eDate))
				stocksnew.add(stock);
		}

		if (type.contentEquals("price")) {
			Map<Integer, Double> value = stocksnew.stream()
					.collect(Collectors.groupingBy(Stock::getWeek, Collectors.averagingDouble(Stock::getClose)));

			Map<Integer, Double> weekly = value.entrySet().stream().sorted(comparingByKey())
					.collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
			return weekly;

		}

		else if (type.contentEquals("volume")) {
			Map<Integer, Double> value = stocksnew.stream()
					.collect(Collectors.groupingBy(Stock::getWeek, Collectors.averagingDouble(Stock::getVolume)));

			Map<Integer, Double> weekly = value.entrySet().stream().sorted(comparingByKey())
					.collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
			return weekly;
		}

		else {

			System.out.print("Enter correct parameters");
			return null;
		}

	}

/////////////////////                 MONTHLY COMPANY              ///////////////////////------p
	public Map<String, Double> MonthlyCompany(String ticker, String startDate, String endDate, String type)
			throws ParseException {
		Company company = getByTicker(ticker);
		List<Stock> stocks = company.getStocks();
		List<Stock> stocksnew = new ArrayList<>();
		Date sDate = converter.parse(startDate);
		Date eDate = converter.parse(endDate);
		for (Stock stock : stocks) {
			Date nowDate = converter.parse(stock.getDate());
			if ((nowDate.after(sDate) && nowDate.before(eDate)) || nowDate.equals(sDate) || nowDate.equals(eDate)) {
				stocksnew.add(stock);
			}
		}

		if (type.contentEquals("price")) {
			Map<String, Double> monthly = stocksnew.stream()
					.collect(Collectors.groupingBy(Stock::getMonth, Collectors.averagingDouble(Stock::getClose)));

			return monthly;

		}

		else if (type.contentEquals("volume")) {
			Map<String, Double> monthly = stocksnew.stream()
					.collect(Collectors.groupingBy(Stock::getMonth, Collectors.averagingDouble(Stock::getVolume)));

			return monthly;

		}

		else {

			System.out.print("Enter correct parameters");
			return null;
		}

	}
////////////////////////                WEEKLY SECTOR         ////////////////////////----------p

	public Map<Integer, Double> WeeklySector(String sector, String startDate, String endDate, String type)
			throws ParseException {
		List<Company> companies = getBySector(sector);
		List<Stock> stocks = new ArrayList<>();
		for (Company company : companies) {
			stocks.addAll(company.getStocks());
		}
		List<Stock> stocksnew = new ArrayList<>();
		Date sDate = converter.parse(startDate);
		Date eDate = converter.parse(endDate);
		for (Stock stock : stocks) {
			Date nowDate = converter.parse(stock.getDate());
			if ((nowDate.after(sDate) && nowDate.before(eDate)) || nowDate.equals(sDate) || nowDate.equals(eDate)) {
				stocksnew.add(stock);
			}
		}

		if (type.contentEquals("price")) {
			Map<Integer, Double> value = stocksnew.stream()
					.collect(Collectors.groupingBy(Stock::getWeek, Collectors.averagingDouble(Stock::getClose)));

			Map<Integer, Double> weekly = value.entrySet().stream().sorted(comparingByKey())
					.collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

			return weekly;

		}

		else if (type.contentEquals("volume")) {
			Map<Integer, Double> value = stocksnew.stream()
					.collect(Collectors.groupingBy(Stock::getWeek, Collectors.averagingDouble(Stock::getVolume)));

			Map<Integer, Double> weekly = value.entrySet().stream().sorted(comparingByKey())
					.collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

			return weekly;

		}

		else {
			System.out.print("Enter correct parameters");
			return null;
		}

	}

//////////////////////////////////              MONTHLY SECTOR          ////////////////////////////////////------p
	public Map<String, Double> MonthlySector(String sector, String startDate, String endDate, String type)
			throws ParseException {
		List<Company> companies = getBySector(sector);
		List<Stock> stocks = new ArrayList<>();
		for (Company company : companies) {
			stocks.addAll(company.getStocks());
		}
		List<Stock> stocksnew = new ArrayList<>();
		Date sDate = converter.parse(startDate);
		Date eDate = converter.parse(endDate);
		for (Stock stock : stocks) {
			Date nowDate = converter.parse(stock.getDate());
			if ((nowDate.after(sDate) && nowDate.before(eDate)) || nowDate.equals(sDate) || nowDate.equals(eDate)) {
				stocksnew.add(stock);
			}
		}

		if (type.contentEquals("price")) {
			Map<String, Double> monthly = stocksnew.stream()
					.collect(Collectors.groupingBy(Stock::getMonth, Collectors.averagingDouble(Stock::getClose)));

			return monthly;

		}

		else if (type.contentEquals("volume")) {
			Map<String, Double> monthly = stocksnew.stream()
					.collect(Collectors.groupingBy(Stock::getMonth, Collectors.averagingDouble(Stock::getVolume)));

			return monthly;
		}

		else {
			System.out.print("Enter correct parameters");
			return null;
		}

	}

///////////////////////    FUNCTION FOR DAILY WEEKLY MONTHLY FOR A SECTOR               ///////////////////////////----p
	public Map<String, Double> DataSector(String sector, String startDate, String endDate, String type, String range)
			throws ParseException {
		if (range.contentEquals("daily")) {
			return DailySector(sector, startDate, endDate, type);
		}

		/*
		 * else if (range.contentEquals("weekly")) { return WeeklySector(sector,
		 * startDate, endDate, type); }
		 */

		else if (range.contentEquals("monthly")) {
			return MonthlySector(sector, startDate, endDate, type);
		}

		else {

			System.out.print("Enter correct parameters");
			return null;
		}
	}

/////////////////////          FUNCTION FOR DAILY WEEKLY MONTHLY FOR A COMPANY           ////////////////////////////
	public Map<String, Double> DataCompany(String ticker, String startDate, String endDate, String type, String range)
			throws ParseException {
		if (range.contentEquals("daily")) {
			return DailyCompany(ticker, startDate, endDate, type);
		}

		/*
		 * else if (range.contentEquals("weekly")) { return WeeklyCompany(ticker,
		 * startDate, endDate, type); }
		 */
		else if (range.contentEquals("monthly")) {
			return MonthlyCompany(ticker, startDate, endDate, type);
		}

		else {

			System.out.print("Enter correct parameters");
			return null;
		}
	}

	// List of objects with daily data for a company grid grid grid
	public List<DailyData> gridCompany(String ticker, String startDate, String endDate) throws ParseException {

		Company company = getByTicker(ticker);
		List<Stock> stocks = company.getStocks();
		List<DailyData> objList = new ArrayList<>();

		Date sDate = converter.parse(startDate);
		Date eDate = converter.parse(endDate);

		for (Stock stock : stocks) {

			Date nowDate = converter.parse(stock.getDate());
			if ((nowDate.after(sDate) && nowDate.before(eDate)) || nowDate.equals(sDate) || nowDate.equals(eDate)) {

				DailyData dailyData = new DailyData();
				dailyData.setDate(stock.getDate());
				dailyData.setPrice(stock.getClose());
				dailyData.setVolume(stock.getVolume());
				dailyData.setCompanyName(company.getName());
				dailyData.setSector(company.getSector());
				dailyData.setTicker(company.getTicker());
				objList.add(dailyData);
			}
		}
		return objList;
	}

	// List of objects with daily data for a sector
	public List<List<DailyData>> gridSector(String sector, String startDate, String endDate) throws ParseException {
		List<Company> companies = getBySector(sector);
		List<List<DailyData>> nestedList = new ArrayList<List<DailyData>>();
		for (Company company : companies) {
			nestedList.add(gridCompany(company.getTicker(), startDate, endDate));
		}
		return nestedList;
	}

	// Companies with sector selected, returns only companies----p
	public Map<String, List<Double>> ChartCompanySector(List<String> tickerList, List<String> sectorList, String type) {

		Map<String, List<Double>> Map3 = new HashMap<>();
		for (String ticker : tickerList) {
			Company company = getByTicker(ticker);
			if (sectorList.contains(company.getSector())) {

				AverageValues obj = CompanyAverage(ticker, type);
				Map3.put(company.getName(), Arrays.asList(obj.getPreCovidValue(), obj.getPostCovidValue()));
			}

		}

		return Map3;
	}

	// Companies with sector selected, returns companies and avg values of
	// sectors----------p
	public Map<String, List<Double>> AvgChartCompanySector(List<String> tickerList, List<String> sectorList,
			String type) {

		List<String> sectors = new ArrayList<>();

		Map<String, List<Double>> Map3 = new HashMap<>();
		for (String ticker : tickerList) {
			Company company = getByTicker(ticker);
			if (sectorList.contains(company.getSector())) {
				sectors.add(company.getSector());
				AverageValues obj = CompanyAverage(ticker, type);
				Map3.put(company.getName(), Arrays.asList(obj.getPreCovidValue(), obj.getPostCovidValue()));
			}

		}
		for (String sector : sectors) {
			AverageValues obj = SectorAverage(sector, type);
			Map3.put(sector, Arrays.asList(obj.getPreCovidValue(), obj.getPostCovidValue()));
		}
		return Map3;
	}

	// Companies Selected return only companies-----p

	public Map<String, List<Double>> ChartCompany(List<String> tickerList, String type) {
		Map<String, List<Double>> Map3 = new HashMap<>();
		for (String ticker : tickerList) {
			Company company = getByTicker(ticker);
			AverageValues obj = CompanyAverage(ticker, type);
			Map3.put(company.getName(), Arrays.asList(obj.getPreCovidValue(), obj.getPostCovidValue()));
		}
		return Map3;
	}

	// Sector Selected return only Avg values of sectors------p

	public Map<String, List<Double>> ChartSector(List<String> sectorList, String type) {
		Map<String, List<Double>> Map3 = new HashMap<>();
		for (String sector : sectorList) {
			AverageValues obj = SectorAverage(sector, type);
			Map3.put(sector, Arrays.asList(obj.getPreCovidValue(), obj.getPostCovidValue()));
		}
		return Map3;
	}

	// grid grid grid main
	public List<DailyData> getGridData(String startDate, String endDate, List<String> gotTickers,
			List<String> gotSectors) throws ParseException {

		List<DailyData> allCompanies = new ArrayList<>();
		Set<String> filteredTickers = new HashSet<>();
		Set<String> filteredSectors = new HashSet<>();
		if (gotSectors.isEmpty()) {
			for (String ticker : gotTickers) {
				allCompanies.addAll(gridCompany(ticker, startDate, endDate));
			}
			Collections.sort(allCompanies);
			return allCompanies;
		} else if (gotTickers.isEmpty()) {
			for (String sector : gotSectors) {
				List<Company> companies = getBySector(sector);
				for (Company company : companies) {
					filteredTickers.add(company.getTicker());
				}
			}
			for (String ticker : filteredTickers) {
				allCompanies.addAll(gridCompany(ticker, startDate, endDate));
			}
			Collections.sort(allCompanies);
			return allCompanies;
		}

		else if (gotTickers.size() != 0 && gotSectors.size() != 0) {
			for (String ticker : gotTickers) {
				Company company = getByTicker(ticker);
				if (gotSectors.contains(company.getSector())) {
					filteredSectors.add(company.getSector());
				}
			}
			for (String sector : filteredSectors) {
				List<Company> companies = getBySector(sector);
				for (Company company : companies) {
					filteredTickers.add(company.getTicker());
				}
			}
			for (String ticker : filteredTickers) {
				allCompanies.addAll(gridCompany(ticker, startDate, endDate));
			}
			Collections.sort(allCompanies);
			return allCompanies;
		} else {
			return null;
		}

	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////-----sab dh

	private String[] colour_array = { "#FFCDD2", "#F8BBD0", "#E1BEE7", "#D1C4E9", "#C5CAE9", "#B3E5FC", "#B2DFDB",
			"#FFECB3", "#FFCCBC", "#D7CCC8", "#F06292", "#64B5F6", "#FFCA28", "#8BC34A", "#A1887F", "#B71C1C",
			"#4A148C", "#CD5C5C", "#EC407A", "#7CB342", "#9CCC65", "#F08080", "#FFA07A", "#808080", "#000080",
			"#FF00FF", "#800080", "#00FFFF", "#008000", "#FFFF00", "#800000", "#FFC0CB", "#CD5C5C", "#F08080",
			"#FA8072", "#E9967A", "#FFA07A", "#DC143C", "#FF0000", "#B22222", "#8B0000", "#FFC0CB", "#FFB6C1",
			"#FF69B4", "#FF1493", "#C71585", "#DB7093", "#FFA07A", "#FF7F50", "#FF6347", "#FF4500", "#FF8C00",
			"#FFA500", "#FF69B4", "#FFA500", "#9400D3", "#7CFC00", "#2E8B57", "#191970", "#CD853F", "#800000",
			"#00FFFF", "#4682B4", "#00BFFF", "#4169E1", "#F4A460" };

	// ONLY COMPANIES (PRE POST)

	public List<ChartObject> getChartCompany(List<String> tickerList, String type) {
		int i = 0;
		List<ChartObject> chart = new ArrayList<>();

		for (String ticker : tickerList) {
			Company company = getByTicker(ticker);
			AverageValues obj = CompanyAverage(ticker, type);

			ChartObject object = new ChartObject();
			i++;
			object.setLabel(company.getName());
			object.setBorderColor(colour_array[i]);
			object.setBackgroundColor(colour_array[i]);
			object.setData(Arrays.asList(obj.getPreCovidValue(), obj.getPostCovidValue()));

			chart.add(object);
		}

		return chart;
	}

	// COMPANIES WITH SECTOR, RETURN ONLY COMPANIES(PRE POST)
	public List<ChartObject> getChartCompanySector(List<String> tickerList, List<String> sectorList, String type) {

		List<ChartObject> chart = new ArrayList<>();
		int i = 0;
		for (String ticker : tickerList) {
			Company company = getByTicker(ticker);

			if (sectorList.contains(company.getSector())) {
				AverageValues obj = CompanyAverage(ticker, type);

				ChartObject object = new ChartObject();
				i++;
				object.setLabel(company.getName());
				object.setBorderColor(colour_array[i]);
				object.setBackgroundColor(colour_array[i]);
				object.setData(Arrays.asList(obj.getPreCovidValue(), obj.getPostCovidValue()));

				chart.add(object);
			}

		}
		return chart;
	}

	// COMPANIES WITH SECTOR, RETURN COMPANIES AND SECTORS (PRE POST)
	public List<ChartObject> getAvgChartCompanySector(List<String> tickerList, List<String> sectorList, String type) {
		int i = 0;
		List<ChartObject> chart = new ArrayList<>();
		List<String> sectors = new ArrayList<>();
		for (String ticker : tickerList) {
			Company company = getByTicker(ticker);

			if (sectorList.contains(company.getSector())) {
				AverageValues obj = CompanyAverage(ticker, type);
				sectors.add(company.getSector());
				ChartObject object = new ChartObject();
				i++;
				object.setLabel(company.getName());
				object.setBorderColor(colour_array[i]);
				object.setBackgroundColor(colour_array[i]);
				object.setData(Arrays.asList(obj.getPreCovidValue(), obj.getPostCovidValue()));

				chart.add(object);
			}

		}

		List<String> Sectors = new ArrayList<>(new HashSet<String>(sectors));
		for (String sector : Sectors) {
			AverageValues obj = SectorAverage(sector, type);

			ChartObject object = new ChartObject();
			i++;
			object.setLabel(sector);
			object.setBorderColor(colour_array[i]);
			object.setBackgroundColor(colour_array[i]);
			object.setData(Arrays.asList(obj.getPreCovidValue(), obj.getPostCovidValue()));

			chart.add(object);
		}
		return chart;
	}

	// ONLY SECTORS PRE POST
	public List<ChartObject> getChartSector(List<String> sectorList, String type) {

		List<ChartObject> chart = new ArrayList<>();
		int i = 0;
		for (String sector : sectorList) {

			AverageValues obj = SectorAverage(sector, type);

			ChartObject object = new ChartObject();
			i++;
			object.setLabel(sector);
			object.setBorderColor(colour_array[i]);
			object.setBackgroundColor(colour_array[i]);
			object.setData(Arrays.asList(obj.getPreCovidValue(), obj.getPostCovidValue()));

			chart.add(object);
		}

		return chart;
	}

	// ONLY COMPANIES (WEEKLY)

	public ChartObjectCustom WeeklyCompanyObject(List<String> tickerList, String startDate, String endDate, String type)
			throws ParseException {
		int i = 0;
		ChartObjectCustom value = new ChartObjectCustom();
		List<ChartObject> chart = new ArrayList<>();
		List<String> labels = new ArrayList<>();
		List<Integer> key_values = new ArrayList<>();

		for (String ticker : tickerList) {
			i++;
			Company company = getByTicker(ticker);
			Map<Integer, Double> obj = WeeklyCompany(ticker, startDate, endDate, type);
			if (i == 1)
				key_values = new ArrayList<Integer>(obj.keySet());

			List<Double> valueList = new ArrayList<Double>(obj.values());
			ChartObject object = new ChartObject();

			object.setLabel(company.getName());
			object.setBorderColor(colour_array[i]);
			object.setBackgroundColor(colour_array[i]);
			object.setData(valueList);
			chart.add(object);

		}
		for (int k = 0; k < key_values.size(); k++)
			labels.add("week" + Integer.toString(key_values.get(k)));
		value.setLabels(labels);
		value.setDatasets(chart);

		return value;
	}

	// COMPANIES WITH SECTOR, RETURN ONLY COMPANIES(WEEKLY)

	public ChartObjectCustom WeeklyCompanySectorObject(List<String> tickerList, List<String> sectorList,
			String startDate, String endDate, String type) throws ParseException {

		ChartObjectCustom value = new ChartObjectCustom();
		List<ChartObject> chart = new ArrayList<>();
		List<String> labels = new ArrayList<>();
		List<Integer> key_values = new ArrayList<>();

		int i = 0;
		for (String ticker : tickerList) {
			i++;
			Company company = getByTicker(ticker);

			if (sectorList.contains(company.getSector())) {
				Map<Integer, Double> obj = WeeklyCompany(ticker, startDate, endDate, type);
				if (i == 1)
					key_values = new ArrayList<Integer>(obj.keySet());

				List<Double> valueList = new ArrayList<Double>(obj.values());
				ChartObject object = new ChartObject();

				object.setLabel(company.getName());
				object.setBorderColor(colour_array[i]);
				object.setBackgroundColor(colour_array[i]);
				object.setData(valueList);
				chart.add(object);
			}

		}

		for (int k = 0; k < key_values.size(); k++)
			labels.add("week" + Integer.toString(key_values.get(k)));

		value.setLabels(labels);
		value.setDatasets(chart);

		return value;

	}

	// COMPANIES WITH SECTOR, RETURN COMPANIES AND SECTORS (WEEKLY)
	public ChartObjectCustom WeeklyAvgCompanySectorObject(List<String> tickerList, List<String> sectorList,
			String startDate, String endDate, String type) throws ParseException {
		int i = 0;
		List<String> sectors = new ArrayList<>();
		ChartObjectCustom value = new ChartObjectCustom();
		List<ChartObject> chart = new ArrayList<>();
		List<String> labels = new ArrayList<>();
		List<Integer> key_values = new ArrayList<>();

		for (String ticker : tickerList) {
			Company company = getByTicker(ticker);
			if (sectorList.contains(company.getSector())) {
				i++;
				sectors.add(company.getSector());
				Map<Integer, Double> obj = WeeklyCompany(ticker, startDate, endDate, type);
				if (i == 1)
					key_values = new ArrayList<Integer>(obj.keySet());

				List<Double> valueList = new ArrayList<Double>(obj.values());
				ChartObject object = new ChartObject();

				object.setLabel(company.getName());
				object.setBorderColor(colour_array[i]);
				object.setBackgroundColor(colour_array[i]);
				object.setData(valueList);
				chart.add(object);

			}
		}

		List<String> Sectors = new ArrayList<>(new HashSet<String>(sectors));
		for (String sector : Sectors) {
			i++;
			Map<Integer, Double> obj = WeeklySector(sector, startDate, endDate, type);
			List<Double> valueList = new ArrayList<Double>(obj.values());
			ChartObject object = new ChartObject();
			object.setLabel(sector);
			object.setBorderColor(colour_array[i]);
			object.setBackgroundColor(colour_array[i]);
			object.setData(valueList);
			chart.add(object);
		}

		for (int k = 0; k < key_values.size(); k++)
			labels.add("week" + Integer.toString(key_values.get(k)));

		value.setLabels(labels);
		value.setDatasets(chart);

		return value;

	}

	// ONLY SECTORS (WEEKLY)
	public ChartObjectCustom WeeklySectorObject(List<String> sectorList, String startDate, String endDate, String type)
			throws ParseException {

		ChartObjectCustom value = new ChartObjectCustom();
		List<ChartObject> chart = new ArrayList<>();
		List<String> labels = new ArrayList<>();
		List<Integer> key_values = new ArrayList<>();
		int i = 0;
		for (String sector : sectorList) {
			i++;
			Map<Integer, Double> obj = WeeklySector(sector, startDate, endDate, type);
			if (i == 1)
				key_values = new ArrayList<Integer>(obj.keySet());

			List<Double> valueList = new ArrayList<Double>(obj.values());
			ChartObject object = new ChartObject();
			object.setLabel(sector);
			object.setBorderColor(colour_array[i]);
			object.setBackgroundColor(colour_array[i]);
			object.setData(valueList);
			chart.add(object);
		}

		for (int k = 0; k < key_values.size(); k++)
			labels.add("week" + Integer.toString(key_values.get(k)));
		value.setLabels(labels);
		value.setDatasets(chart);

		return value;

	}



	/////// DAILY 1. ONLY COMPANIES
	public ChartObjectCustom DailyCompanyObject(List<String> tickerList, String startDate, String endDate, String type)
			throws ParseException {

		Date sDate = converter.parse(startDate);
		Date eDate = converter.parse(endDate);
		int i = 0;
		ChartObjectCustom value = new ChartObjectCustom();
		List<ChartObject> chart = new ArrayList<>();
		List<String> labels = new ArrayList<>();
		List<Stock> stocknew = new ArrayList<>();
		ArrayList<String> keyList = new ArrayList<>();
		ArrayList<Double> valueList = new ArrayList<>();
		for (String ticker : tickerList) {
			Company company = getByTicker(ticker);

			List<Stock> stocks = company.getStocks();

			for (Stock stock : stocks) {

				Date nDate = converter.parse(stock.getDate());
				if (nDate.before(eDate) && nDate.after(sDate) || nDate.equals(sDate) || nDate.equals(eDate)) {
					stocknew.add(stock);
				}
			}

			ChartObject obj = new ChartObject();
			i++;
			obj.setLabel(company.getName());
			obj.setBackgroundColor(colour_array[i]);
			obj.setBorderColor(colour_array[i]);

			if (type.contentEquals("price")) {
				Map<String, Double> daily = stocknew.stream()
						.collect(Collectors.groupingBy(Stock::getDate, Collectors.averagingDouble(Stock::getClose)))
						.entrySet().stream().sorted(comparingByKey())
						.collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

				keyList = new ArrayList<String>(daily.keySet());
				valueList = new ArrayList<Double>(daily.values());
			}

			else if (type.contentEquals("volume")) {
				Map<String, Double> daily = stocknew.stream()
						.collect(Collectors.groupingBy(Stock::getDate, Collectors.averagingDouble(Stock::getVolume)))
						.entrySet().stream().sorted(comparingByKey())
						.collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
				keyList = new ArrayList<String>(daily.keySet());
				valueList = new ArrayList<Double>(daily.values());
			} else {
				System.out.print("Enter correct parameters");
			}
			obj.setData(valueList);
			chart.add(obj);
		}

		value.setDatasets(chart);
		labels = keyList;
		value.setLabels(labels);

		return value;
	}

/////// 	DAILY 2. ONLY SECTORS
	public ChartObjectCustom DailySectorObject(List<String> sectorList, String startDate, String endDate, String type)
			throws ParseException {

		Date sDate = converter.parse(startDate);
		Date eDate = converter.parse(endDate);
		int i = 50;
		ChartObjectCustom value = new ChartObjectCustom();
		List<ChartObject> chart = new ArrayList<>();
		List<String> labels = new ArrayList<>();
		List<Stock> stocknew = new ArrayList<>();
		ArrayList<String> keyList = new ArrayList<>();
		ArrayList<Double> valueList = new ArrayList<>();

		for (String sector : sectorList) {

			List<Company> company = getBySector(sector);
			for (Company comp : company) {
				List<Stock> stocks = comp.getStocks();
				for (Stock stock : stocks) {
					Date nDate = converter.parse(stock.getDate());
					if (nDate.before(eDate) && nDate.after(sDate) || nDate.equals(sDate) || nDate.equals(eDate)) {
						stocknew.add(stock);
					}
				}
			}

			ChartObject obj = new ChartObject();
			i--;
			obj.setLabel(sector);
			obj.setBackgroundColor(colour_array[i]);
			obj.setBorderColor(colour_array[i]);
			if (type.contentEquals("price")) {
				Map<String, Double> daily = stocknew.stream()
						.collect(Collectors.groupingBy(Stock::getDate, Collectors.averagingDouble(Stock::getClose)))
						.entrySet().stream().sorted(comparingByKey())
						.collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

				keyList = new ArrayList<String>(daily.keySet());
				valueList = new ArrayList<Double>(daily.values());
			}

			else if (type.contentEquals("volume")) {
				Map<String, Double> daily = stocknew.stream()
						.collect(Collectors.groupingBy(Stock::getDate, Collectors.averagingDouble(Stock::getVolume)))
						.entrySet().stream().sorted(comparingByKey())
						.collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
				keyList = new ArrayList<String>(daily.keySet());
				valueList = new ArrayList<Double>(daily.values());
			} else {
				System.out.print("Enter correct parameters");
			}
			obj.setData(valueList);
			chart.add(obj);

		}

		value.setDatasets(chart);
		labels = keyList;
		value.setLabels(labels);

		return value;
	}

	//////////// DAILY 3. COMPANY AND SECTOR RETURN MATCHED COMPANIES

	public ChartObjectCustom DailyCompanySectorObject(List<String> tickerList, List<String> sectorList,
			String startDate, String endDate, String type) throws ParseException {

		List<String> tickerNew = new ArrayList<>();
		ChartObjectCustom value = new ChartObjectCustom();
		for (String ticker : tickerList) {

			Company company = getByTicker(ticker);
			if (sectorList.contains(company.getSector()))
				
			{
				tickerNew.add(ticker);

			}
		}
		value = DailyCompanyObject(tickerNew, startDate, endDate, type);
		return value;
	}

	//////////// DAILY 4. COMPANY AND SECTOR RETURN MATCHED COMPANIES AND SECTORS

	public ChartObjectCustom DailyAvgCompanySectorObject(List<String> tickerList, List<String> sectorList,
			String startDate, String endDate, String type) throws ParseException {

		List<String> tickerNew = new ArrayList<>();
		List<String> sectorNew = new ArrayList<>();
		ChartObjectCustom value1 = new ChartObjectCustom();
		ChartObjectCustom value2 = new ChartObjectCustom();
		for (String ticker : tickerList) {

			Company company = getByTicker(ticker);
			if (sectorList.contains(company.getSector())) {
				tickerNew.add(ticker);
				sectorNew.add(company.getSector());

			}
		}
		List<String> SectorNew = new ArrayList<>(new HashSet<String>(sectorNew));
		value1 = DailyCompanyObject(tickerNew, startDate, endDate, type);
		value2 = DailySectorObject(SectorNew, startDate, endDate, type);
		List<ChartObject> obj1 = value1.getDatasets();
		List<ChartObject> obj2 = value2.getDatasets();
		obj1.addAll(obj2);
		value1.setDatasets(obj1);
		return value1;
	}

	/////// MONTHLY 1. ONLY COMPANIES
	public ChartObjectCustom MonthlyCompanyObject(List<String> tickerList, String startDate, String endDate,
			String type) throws ParseException {

		String[] monthList = { "December", "January", "February", "March", "April", "May", "June", "July", "August",
				"September", "October", "November", "December" };
		Date sDate = converter.parse(startDate);
		Date eDate = converter.parse(endDate);
		int i = 0;
		ChartObjectCustom value = new ChartObjectCustom();
		List<ChartObject> chart = new ArrayList<>();
		List<String> labels = new ArrayList<>();
		List<Stock> stocknew = new ArrayList<>();
		ArrayList<String> keyList = new ArrayList<>();
		ArrayList<Double> valueList = new ArrayList<>();
		for (String ticker : tickerList) {
			Company company = getByTicker(ticker);

			List<Stock> stocks = company.getStocks();

			for (Stock stock : stocks) {

				Date nDate = converter.parse(stock.getDate());
				if (nDate.before(eDate) && nDate.after(sDate) || nDate.equals(sDate) || nDate.equals(eDate)) {
					stocknew.add(stock);
				}
			}

			ChartObject obj = new ChartObject();
			i++;
			obj.setLabel(company.getName());
			obj.setBackgroundColor(colour_array[i]);
			obj.setBorderColor(colour_array[i]);

			if (type.contentEquals("price")) {
				Map<String, Double> daily = stocknew.stream()
						.collect(Collectors.groupingBy(Stock::getMonth, Collectors.averagingDouble(Stock::getClose)))
						.entrySet().stream().sorted(comparingByKey())
						.collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

				keyList = new ArrayList<String>(daily.keySet());
				valueList = new ArrayList<Double>(daily.values());
			}

			else if (type.contentEquals("volume")) {
				Map<String, Double> daily = stocknew.stream()
						.collect(Collectors.groupingBy(Stock::getMonth, Collectors.averagingDouble(Stock::getVolume)))
						.entrySet().stream().sorted(comparingByKey())
						.collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
				keyList = new ArrayList<String>(daily.keySet());
				valueList = new ArrayList<Double>(daily.values());
			} else {
				System.out.print("Enter correct parameters");
			}
			obj.setData(valueList);
			chart.add(obj);
		}

		value.setDatasets(chart);
		for (int j = 0; j < keyList.size(); j++) {

			int index = Integer.parseInt(keyList.get(j)) % 12;
			labels.add(monthList[index]);
		}

		value.setLabels(labels);

		return value;
	}

/////// 	MONTHLY 2. ONLY SECTORS
	public ChartObjectCustom MonthlySectorObject(List<String> sectorList, String startDate, String endDate, String type)
			throws ParseException {

		String[] monthList = { "December", "January", "February", "March", "April", "May", "June", "July", "August",
				"September", "October", "November", "December" };
		Date sDate = converter.parse(startDate);
		Date eDate = converter.parse(endDate);
		int i = 50;
		ChartObjectCustom value = new ChartObjectCustom();
		List<ChartObject> chart = new ArrayList<>();
		List<String> labels = new ArrayList<>();
		List<Stock> stocknew = new ArrayList<>();
		ArrayList<String> keyList = new ArrayList<>();
		ArrayList<Double> valueList = new ArrayList<>();

		for (String sector : sectorList) {

			List<Company> company = getBySector(sector);
			for (Company comp : company) {
				List<Stock> stocks = comp.getStocks();
				for (Stock stock : stocks) {
					Date nDate = converter.parse(stock.getDate());
					if (nDate.before(eDate) && nDate.after(sDate) || nDate.equals(sDate) || nDate.equals(eDate)) {
						stocknew.add(stock);
					}
				}
			}

			ChartObject obj = new ChartObject();
			i--;
			obj.setLabel(sector);
			obj.setBackgroundColor(colour_array[i]);
			obj.setBorderColor(colour_array[i]);
			if (type.contentEquals("price")) {
				Map<String, Double> daily = stocknew.stream()
						.collect(Collectors.groupingBy(Stock::getMonth, Collectors.averagingDouble(Stock::getClose)))
						.entrySet().stream().sorted(comparingByKey())
						.collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

				keyList = new ArrayList<String>(daily.keySet());
				valueList = new ArrayList<Double>(daily.values());
			}

			else if (type.contentEquals("volume")) {
				Map<String, Double> daily = stocknew.stream()
						.collect(Collectors.groupingBy(Stock::getMonth, Collectors.averagingDouble(Stock::getVolume)))
						.entrySet().stream().sorted(comparingByKey())
						.collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
				keyList = new ArrayList<String>(daily.keySet());
				valueList = new ArrayList<Double>(daily.values());
			} else {
				System.out.print("Enter correct parameters");
			}
			obj.setData(valueList);
			chart.add(obj);

		}

		value.setDatasets(chart);
		for (int j = 0; j < keyList.size(); j++) {

			int index = Integer.parseInt(keyList.get(j)) % 12;
			labels.add(monthList[index]);
		}

		value.setLabels(labels);

		return value;
	}

////////////MONTHLY 3. COMPANY AND SECTOR RETURN MATCHED COMPANIES	

	public ChartObjectCustom MonthlyCompanySectorObject(List<String> tickerList, List<String> sectorList,
			String startDate, String endDate, String type) throws ParseException {

		List<String> tickerNew = new ArrayList<>();
		ChartObjectCustom value = new ChartObjectCustom();
		for (String ticker : tickerList) {

			Company company = getByTicker(ticker);
			if (sectorList.contains(company.getSector()))
				
			{
				tickerNew.add(ticker);

			}
		}
		value = MonthlyCompanyObject(tickerNew, startDate, endDate, type);
		return value;
	}

//////////// MONTHLY 4. COMPANY AND SECTOR RETURN MATCHED COMPANIES AND SECTORS	

	public ChartObjectCustom MonthlyAvgCompanySectorObject(List<String> tickerList, List<String> sectorList,
			String startDate, String endDate, String type) throws ParseException {

		List<String> tickerNew = new ArrayList<>();
		List<String> sectorNew = new ArrayList<>();
		ChartObjectCustom value1 = new ChartObjectCustom();
		ChartObjectCustom value2 = new ChartObjectCustom();
		for (String ticker : tickerList) {

			Company company = getByTicker(ticker);
			if (sectorList.contains(company.getSector())) {
				tickerNew.add(ticker);
				sectorNew.add(company.getSector());

			}
		}
		List<String> SectorNew = new ArrayList<>(new HashSet<String>(sectorNew));
		value1 = MonthlyCompanyObject(tickerNew, startDate, endDate, type);
		value2 = MonthlySectorObject(SectorNew, startDate, endDate, type);
		List<ChartObject> obj1 = value1.getDatasets();
		List<ChartObject> obj2 = value2.getDatasets();
		obj1.addAll(obj2);
		value1.setDatasets(obj1);
		return value1;
	}

}
