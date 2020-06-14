package com.restapi.market.service;

import java.text.NumberFormat;
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

import javax.annotation.PostConstruct;

import static java.util.stream.Collectors.*;
import static java.util.Map.Entry.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class CompanyService  {

	Logger logger = LoggerFactory.getLogger(CompanyService.class);

	@Value("${token}")
	private String token;

	private static String url1 = "https://sandbox.iexapis.com/stable/stock/";
	private static String url2Initial = "/chart/ytd?chartCloseOnly=true&token=";
	private static String url2New = "/chart/ytd?chartLast=1&chartCloseOnly=true&token=";
	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private CompanyRepository companyRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	SimpleDateFormat formatYMD = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat formatDMY = new SimpleDateFormat("dd-MM-yyyy");
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
	public void addStocksByTicker(String ticker) throws ParseException {
		Company company = this.companyRepository.findByTicker(ticker);
		Stock[] stocks = restTemplate.getForObject(url1 + ticker + url2Initial + token, Stock[].class);
		for (Stock stock : stocks) {
			Date nowDate = formatYMD.parse(stock.getDate());

			cal.setTime(nowDate);
			int week = cal.get(Calendar.WEEK_OF_YEAR);
			stock.setWeek(week);
			stock.setMonth(stock.getDate().substring(5, 7));

		}
		company.setStocks(Arrays.asList(stocks));
		this.companyRepository.save(company);
		
	}

	// seed database with data of all companies
	@PostConstruct
	public void seedDb() {
		logger.info("Seeding Initiated");
		List<String> tickers = mongoTemplate.query(Company.class).distinct("ticker").as(String.class).all();
		for (String ticker : tickers) {
			try {
				addStocksByTicker(ticker);
			} catch (Exception exception) {
				logger.error("Could not add %s due to", ticker);
				logger.error(exception.toString());
			}
		}
		logger.info("Seeding Successful!");
	}
	
	

	// daily update of stocks data of company whose ticker is passed
	public void updateByTicker(String ticker) throws ParseException {
		Stock[] stocks = restTemplate.getForObject(url1 + ticker + url2New + token, Stock[].class); // returns only one
																									// object
		for (Stock stock : stocks) {

			Date nowDate = formatYMD.parse(stock.getDate());
			cal.setTime(nowDate);
			int week = cal.get(Calendar.WEEK_OF_YEAR);
			stock.setWeek(week);
			stock.setMonth(stock.getDate().substring(5, 7));

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
				logger.error("Could not update %s due to", ticker);
				logger.error(exception.toString());
			}
		}
		logger.info("Updated database");
	}

	// calculate average volume for a company by ticker
	public AverageValues calAvgVolumeByCompany(String ticker, String boundaryDate) throws ParseException {
		Date thresholdDate = formatYMD.parse(boundaryDate);

		Company company = getByTicker(ticker);
		AverageValues volumeAverage = new AverageValues();
		double preVolumeSum = 0;
		double postVolumeSum = 0;
		int sizeOfPre = 0;
		List<Stock> stocks = company.getStocks();
		for (Stock stock : stocks) {
			Date nowDate = formatYMD.parse(stock.getDate());
			if (nowDate.before(thresholdDate) || nowDate.equals(thresholdDate)) {
				sizeOfPre = sizeOfPre + 1;
				preVolumeSum += stock.getVolume();
			} else {
				postVolumeSum += stock.getVolume();
			}

		}
		if (stocks.isEmpty()) {
			volumeAverage.setPreCovidValue(0);
			volumeAverage.setPostCovidValue(0);
		} else if (sizeOfPre == 0) {
			volumeAverage.setPreCovidValue(0);
			volumeAverage.setPostCovidValue((postVolumeSum) / (stocks.size() - sizeOfPre));
		} else if (sizeOfPre == stocks.size()) {
			volumeAverage.setPreCovidValue((preVolumeSum) / (sizeOfPre));
			volumeAverage.setPostCovidValue(0);
		} else {
			volumeAverage.setPreCovidValue((preVolumeSum) / (sizeOfPre));
			volumeAverage.setPostCovidValue((postVolumeSum) / (stocks.size() - sizeOfPre));
		}

		volumeAverage.setDeviation(volumeAverage.getPostCovidValue() - volumeAverage.getPreCovidValue());

		return volumeAverage;

	}

	// calculate average stock-price for a company by ticker
	public AverageValues calAvgPriceByCompany(String ticker, String boundaryDate) throws ParseException {
		Date thresholdDate = formatYMD.parse(boundaryDate);
		Company company = getByTicker(ticker);
		AverageValues priceAverage = new AverageValues();
		double preCloseSum = 0;
		double postCloseSum = 0;
		int sizeOfPre = 0;

		List<Stock> stocks = company.getStocks();

		for (Stock stock : stocks) {

			Date nowDate = formatYMD.parse(stock.getDate());
			if (nowDate.before(thresholdDate) || nowDate.equals(thresholdDate)) {

				preCloseSum += stock.getClose();
				sizeOfPre = sizeOfPre + 1;
			}

			else {
				postCloseSum += stock.getClose();
			}
		}

		if (stocks.isEmpty()) {
			priceAverage.setPreCovidValue(0);
			priceAverage.setPostCovidValue(0);
		} else if (sizeOfPre == 0) {
			priceAverage.setPreCovidValue(0);
			priceAverage.setPostCovidValue((postCloseSum) / (stocks.size() - sizeOfPre));
		} else if (sizeOfPre == stocks.size()) {
			priceAverage.setPreCovidValue((preCloseSum) / (sizeOfPre));
			priceAverage.setPostCovidValue(0);
		} else {
			priceAverage.setPreCovidValue((preCloseSum) / (sizeOfPre));
			priceAverage.setPostCovidValue((postCloseSum) / (stocks.size() - sizeOfPre));
		}
		priceAverage.setDeviation(priceAverage.getPostCovidValue() - priceAverage.getPreCovidValue());

		return priceAverage;

	}

	// calculate average stock-price for a sector
	public AverageValues calAvgPriceBySector(String sector, String boundaryDate) throws ParseException {
		List<Company> company = getBySector(sector);
		AverageValues priceAverage = new AverageValues();
		double preCloseSum = 0;
		double postCloseSum = 0;

		for (Company comp : company) {

			preCloseSum = preCloseSum + calAvgPriceByCompany(comp.getTicker(), boundaryDate).getPreCovidValue();

			postCloseSum = postCloseSum + calAvgPriceByCompany(comp.getTicker(), boundaryDate).getPostCovidValue();

		}

		if (company.isEmpty()) {
			priceAverage.setPreCovidValue(0);
			priceAverage.setPostCovidValue(0);
		} else {
			priceAverage.setPreCovidValue((preCloseSum) / (company.size()));
			priceAverage.setPostCovidValue((postCloseSum) / (company.size()));
		}

		priceAverage.setDeviation(priceAverage.getPostCovidValue() - priceAverage.getPreCovidValue());

		return priceAverage;

	}

	// calculate average volume for a sector
	public AverageValues calAvgVolumeBySector(String sector, String boundaryDate) throws ParseException {
		List<Company> company = getBySector(sector);

		AverageValues volumeAverage = new AverageValues();
		double preVolumeSum = 0;
		double postVolumeSum = 0;

		for (Company comp : company) {
			preVolumeSum = preVolumeSum + calAvgVolumeByCompany(comp.getTicker(), boundaryDate).getPreCovidValue();

			postVolumeSum = postVolumeSum + calAvgVolumeByCompany(comp.getTicker(), boundaryDate).getPostCovidValue();

		}

		if (company.isEmpty()) {
			volumeAverage.setPreCovidValue(0);
			volumeAverage.setPostCovidValue(0);
		} else {
			volumeAverage.setPreCovidValue((preVolumeSum) / (company.size()));
			volumeAverage.setPostCovidValue((postVolumeSum) / (company.size()));
		}
		volumeAverage.setDeviation(volumeAverage.getPostCovidValue() - volumeAverage.getPreCovidValue());

		return volumeAverage;

	}

	// Calculate average values for a company
	public AverageValues companyAverage(String ticker, String type, String boundaryDate) throws ParseException {

		if (type.contentEquals("price")) {

			return calAvgPriceByCompany(ticker, boundaryDate);
		}

		else if (type.contentEquals("volume")) {

			return calAvgVolumeByCompany(ticker, boundaryDate);
		}

		else {
			return null;
		}
	}

	// Calculate average values for a sector
	public AverageValues sectorAverage(String sector, String type, String boundaryDate) throws ParseException {

		if (type.contentEquals("price")) {

			return calAvgPriceBySector(sector, boundaryDate);
		}

		else if (type.contentEquals("volume")) {

			return calAvgVolumeBySector(sector, boundaryDate);
		}

		else {
			return null;
		}
	}

	// Sort Functions for Sector-wise Deviation:

	// Sort Average Volume Deviation of Sectors
	public Map<String, Double> getSectorVolumeDeviation(String boundaryDate) throws ParseException {
		List<String> sectorList = getAllSectors();
		Map<String, Double> values = new HashMap<>();
		for (String i : sectorList) {
			AverageValues volumeAverage = calAvgVolumeBySector(i, boundaryDate);
			values.put(i, volumeAverage.getDeviation());
		}

		return values.entrySet().stream().sorted(comparingByValue())
				.collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
	}

	// Sort Average stock-price Deviation of Sectors
	public Map<String, Double> getSectorPriceDeviation(String boundaryDate) throws ParseException {
		List<String> sectorList = getAllSectors();
		Map<String, Double> values = new HashMap<>();
		for (String i : sectorList) {

			AverageValues priceAverage = calAvgPriceBySector(i, boundaryDate);
			values.put(i, priceAverage.getDeviation());
		}
		return values.entrySet().stream().sorted(comparingByValue())
				.collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
	}

	// Sort Functions for Company-wise Deviation:

	// Sort Average Volume Deviation of Company
	public Map<String, Double> getCompanyVolumeDeviation(String boundaryDate) throws ParseException {
		List<String> tickerList = getAllTickers();
		Map<String, Double> values = new HashMap<>();
		for (String i : tickerList) {
			AverageValues volumeAverage = calAvgVolumeByCompany(i, boundaryDate);
			values.put(i, volumeAverage.getDeviation());
		}
		return values.entrySet().stream().sorted(comparingByValue())
				.collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
	}

	// Sort Average stock-price Deviation of Company
	public Map<String, Double> getCompanyPriceDeviation(String boundaryDate) throws ParseException {
		List<String> tickerList = getAllTickers();
		Map<String, Double> values = new HashMap<>();

		for (String i : tickerList) {
			AverageValues priceAverage = calAvgPriceByCompany(i, boundaryDate);
			values.put(i, priceAverage.getDeviation());
		}

		return values.entrySet().stream().sorted(comparingByValue())
				.collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
	}

	/*// Sorted Deviation for Companies
	public Map<String, Double> getDeviationCompany(String rank, String boundaryDate) throws ParseException {
		if (rank.contentEquals("volume")) {
			return getCompanyVolumeDeviation(boundaryDate);
		}
		else {
			return getCompanyPriceDeviation(boundaryDate);
		}
	}
	// Sorted Deviation for Sectors
	public Map<String, Double> getDeviationSector(String rank, String boundaryDate) throws ParseException {
		if (rank.contentEquals("volume")) {
			return getSectorVolumeDeviation(boundaryDate);
		}
		else if (rank.contentEquals("price")) {
			return getSectorPriceDeviation(boundaryDate);
		} else {
			return null;
		}
	}*/
	
	
	//Sorted Deviation
	
	public Map<String, Double> getDeviation(String type, String value, String boundaryDate)
			throws ParseException {

		if (value.contentEquals("company")) {
			if (type.contentEquals("volume")) {
				return getCompanyVolumeDeviation(boundaryDate);
			}

			else if (type.contentEquals("price")) {
				return getCompanyPriceDeviation(boundaryDate);
			} else {
				return null;
			}
		}

		else if (value.contentEquals("sector")) {
			if (type.contentEquals("volume")) {
				return getSectorVolumeDeviation(boundaryDate);
			}

			else if (type.contentEquals("price")) {
				return getSectorPriceDeviation(boundaryDate);
			} else {
				return null;
			}
		}

		else {
			return null;
		}

	}
	

	// Calculate Average Stock Price and Volume
	public Calculate averagestock(List<Stock> stocks) {

		Calculate calc = new Calculate();
		double closeSum = 0;
		double volumeSum = 0;
		for (Stock stock : stocks) {
			closeSum += stock.getClose();
			volumeSum += stock.getVolume();
		}
		if (stocks.isEmpty()) {
			calc.setPrice(0);
			calc.setVolume(0);
		} else {
			calc.setPrice(closeSum / stocks.size());
			calc.setVolume(volumeSum / stocks.size());
		}
		return calc;
	}

	// Calculate for Average date range by company public Calculate----summary line
	public Calculate getDataByRangeCompany(String ticker, String startDate, String endDate) throws ParseException {
		Company company = getByTicker(ticker);
		List<Stock> stocks = company.getStocks();
		List<Stock> stocksnew = new ArrayList<>();
		Date eDate = formatYMD.parse(endDate);
		Date sDate = formatYMD.parse(startDate);
		for (Stock stock : stocks) {

			String nDate = stock.getDate();
			Date nowDate = formatYMD.parse(nDate);
			if ((nowDate.before(eDate) && nowDate.after(sDate)) || nowDate.equals(sDate) || nowDate.equals(eDate)) {
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
		Date eDate = formatYMD.parse(endDate);
		Date sDate = formatYMD.parse(startDate);
		for (Company comp : companies) {

			List<Stock> stocks = comp.getStocks();
			for (Stock stock : stocks) {

				String nDate = stock.getDate();
				Date nowDate = formatYMD.parse(nDate);
				if ((nowDate.before(eDate) && nowDate.after(sDate)) || nowDate.equals(sDate) || nowDate.equals(eDate)) {
					stocksnew.add(stock);
				}
			}
		}
		return averagestock(stocksnew);
	}

	// List of objects with daily data for a company (required for grid function)
	public List<DailyData> gridCompany(String ticker, String startDate, String endDate) throws ParseException {
		NumberFormat formatNum = NumberFormat.getInstance();
		formatNum.setGroupingUsed(true);

		Company company = getByTicker(ticker);
		List<Stock> stocks = company.getStocks();
		List<DailyData> objList = new ArrayList<>();

		Date sDate = formatYMD.parse(startDate);
		Date eDate = formatYMD.parse(endDate);

		for (Stock stock : stocks) {

			Date nowDate = formatYMD.parse(stock.getDate());
			if ((nowDate.after(sDate) && nowDate.before(eDate)) || nowDate.equals(sDate) || nowDate.equals(eDate)) {

				DailyData dailyData = new DailyData();
				dailyData.setDate(stock.getDate());
				dailyData.setPrice(formatNum.format(stock.getClose()));
				dailyData.setVolume(formatNum.format(stock.getVolume()));
				dailyData.setCompanyName(company.getName());
				dailyData.setSector(company.getSector());
				dailyData.setTicker(company.getTicker());
				objList.add(dailyData);
			}
		}
		return objList;
	}

	// for displaying data on the grid
	public List<DailyData> getGridData(String startDate, String endDate, List<String> gotTickers,
			List<String> gotSectors) throws ParseException {

		List<DailyData> allCompanies = new ArrayList<>();
		Set<String> filteredSectors = new HashSet<>();
		if (gotSectors.isEmpty()) {
			for (String ticker : gotTickers) {
				allCompanies.addAll(gridCompany(ticker, startDate, endDate));
			}

		} else if (gotTickers.isEmpty()) {
			for (String sector : gotSectors) {
				List<Company> companies = getBySector(sector);
				for (Company company : companies) {
					allCompanies.addAll(gridCompany(company.getTicker(), startDate, endDate));
				}
			}

		}

		else if (!gotTickers.isEmpty() && !gotSectors.isEmpty()) {
			for (String ticker : gotTickers) {
				Company company = getByTicker(ticker);
				if (gotSectors.contains(company.getSector())) {
					filteredSectors.add(company.getSector());
				}
			}
			for (String sector : filteredSectors) {
				List<Company> companies = getBySector(sector);
				for (Company company : companies) {
					allCompanies.addAll(gridCompany(company.getTicker(), startDate, endDate));
				}
			}

		} else {
			allCompanies = null;
		}
		Collections.sort(allCompanies);
		Collections.reverse(allCompanies);
		for (DailyData data : allCompanies) {
			Date nowDate = formatYMD.parse(data.getDate());
			data.setDate(formatDMY.format(nowDate));
		}
		return allCompanies;

	}

	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// -----sab
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// dh

	private String[] colorArray = {
			// 5th
			"#EF5350","#EC407A","#AB47BC","#FF7043","#FFA726","#7E57C2","#FFCA28","#5C6BC0","#FFEE58 ",
			"#42A5F5","#D4E157","#29B6F6","#9CCC65","#26C6DA","#26A69A","#66BB6A",
			// 6th
			"#F44336","#E91E63","#9C27B0","#FF5722","#FF9800","#FFC107","#673AB7","#FFEB3B","#3F51B5","#CDDC39",
			"#2196F3","#8BC34A","#03A9F4","#00BCD4","#4CAF50",
			
			// 4th
			"#E57373","#FF8A65","#F06292","#FFB74D","#BA68C8","#FFD54F","#9575CD","#FFF176","#7986CB","#DCE775",
			"#64B5F6","#AED581","#4DD0E1","#81C784","#4DB6AC",
			
			// 8th
			"#D32F2F","#E64A19","#C2185B","#F57C00","#7B1FA2","#FFA000","#512DA8","#FBC02D","#303F9F","#AFB42B",
			"#1976D2","#689F38","#0288D1","#FBC02D","#0097A7","#00796B",
			
			//2nd
			"#FFCDD2","#FFCCBC","#FFCDD2","#FFE0B2","#E1BEE7","#FFECB3","#D1C4E9","#FFF9C4","#C5CAE9","#F0F4C3",
			"#BBDEFB","#F0F4C3","#B3E5FC","#DCEDC8","#B2EBF2","#B2DFDB",
			
			//last
			"#B71C1C","#BF360C","#880E4F","#E65100","#4A148C","#FF6F00","#311B92","#F57F17","#1A237E","#827717",
			"#0D47A1","#33691E","#01579B","#1B5E20","#006064","#006064",
			
			//3rd
			"#EF9A9A","#FFAB91","#F48FB1","#FFCC80","#CE93D8","#FFE082","#B39DDB","#FFF59D","#9FA8DA","#E6EE9C",
			"#90CAF9","#C5E1A5","#81D4FA","#C5E1A5","#80DEEA","#80CBC4",
			
			//9th
			"#AD1457","#D84315","#AD1457","#EF6C00","#6A1B9A","#FF8F00","#4527A0","#F9A825","#283593","#9E9D24",
			"#1565C0","#558B2F","#0277BD","#2E7D32"};
			

	public ChartObjectCustom getDataCompany(List<String> tickerList, String startDate, String endDate, String type,
			String group, String boundaryDate) throws ParseException {

		Date sDate = formatYMD.parse(startDate);
		Date eDate = formatYMD.parse(endDate);
		int i = 0;
		ChartObjectCustom value = new ChartObjectCustom();
		List<ChartObject> chart = new ArrayList<>();
		List<String> labels = new ArrayList<>();
		List<String> monthLabel = new ArrayList<>();
		List<String> monthlabel = new ArrayList<>();
		List<String> dayLabel = new ArrayList<>();
		List<String> daylabel = new ArrayList<>();
		List<Stock> stocknew = new ArrayList<>();
		ArrayList<Double> valueList = new ArrayList<>();
		List<Integer> weekLabel = new ArrayList<>();
		List<Integer> weeklabel = new ArrayList<>();
		for (String ticker : tickerList) {

			Company company = getByTicker(ticker);

			List<Stock> stocks = company.getStocks();

			for (Stock stock : stocks) {

				Date nDate = formatYMD.parse(stock.getDate());
				if (nDate.before(eDate) && nDate.after(sDate) || nDate.equals(sDate) || nDate.equals(eDate)) {
					stocknew.add(stock);
				}
			}

			ChartObject obj = new ChartObject();

			obj.setLabel(company.getName());
			obj.setBackgroundColor(colorArray[i]);
			obj.setBorderColor(colorArray[i]);
			i++;

			if (group.contentEquals("daily")) {

				if (type.contentEquals("price")) {
					Map<String, Double> daily = stocknew.stream()
							.collect(Collectors.groupingBy(Stock::getDate, Collectors.averagingDouble(Stock::getClose)))
							.entrySet().stream().sorted(comparingByKey())
							.collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

					dayLabel = new ArrayList<>(daily.keySet());
					valueList = new ArrayList<>(daily.values());
				}

				else if (type.contentEquals("volume")) {
					Map<String, Double> daily = stocknew.stream()
							.collect(Collectors.groupingBy(Stock::getDate, Collectors.averagingDouble(Stock::getVolume)))
							.entrySet().stream().sorted(comparingByKey())
							.collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
					dayLabel = new ArrayList<>(daily.keySet());
					valueList = new ArrayList<>(daily.values());
				} else {
					logger.error("Incorrect parameters entered");
				}
				obj.setData(valueList);
				chart.add(obj);

			}

			else if (group.contentEquals("weekly")) {

				if (type.contentEquals("price")) {
					Map<Integer, Double> weekly = stocknew.stream()
							.collect(Collectors.groupingBy(Stock::getWeek, Collectors.averagingDouble(Stock::getClose)))
							.entrySet().stream().sorted(comparingByKey())
							.collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

					weekLabel = new ArrayList<>(weekly.keySet());
					valueList = new ArrayList<>(weekly.values());
				}

				else if (type.contentEquals("volume")) {
					Map<Integer, Double> weekly = stocknew.stream()
							.collect(Collectors.groupingBy(Stock::getWeek, Collectors.averagingDouble(Stock::getVolume)))
							.entrySet().stream().sorted(comparingByKey())
							.collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
					weekLabel = new ArrayList<>(weekly.keySet());
					valueList = new ArrayList<>(weekly.values());
				} else {
					logger.error("Incorrect parameters entered");
				}
				obj.setData(valueList);
				chart.add(obj);

			}

			else if (group.contentEquals("monthly")) {

				if (type.contentEquals("price")) {

					Map<String, Double> monthly = stocknew.stream()
							.collect(Collectors.groupingBy(Stock::getMonth, Collectors.averagingDouble(Stock::getClose)))
							.entrySet().stream().sorted(comparingByKey())
							.collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

					monthLabel = new ArrayList<>(monthly.keySet());
					valueList = new ArrayList<>(monthly.values());
				}

				else if (type.contentEquals("volume")) {
					Map<String, Double> monthly = stocknew.stream()
							.collect(Collectors.groupingBy(Stock::getMonth,
									Collectors.averagingDouble(Stock::getVolume)))
							.entrySet().stream().sorted(comparingByKey())
							.collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
					monthLabel = new ArrayList<>(monthly.keySet());
					valueList = new ArrayList<>(monthly.values());
				} else {
					logger.error("Incorrect parameters entered");
				}
				obj.setData(valueList);
				chart.add(obj);

			}

			else if (group.contentEquals("covid")) {
				AverageValues val = companyAverage(ticker, type, boundaryDate);
				obj.setData(Arrays.asList(val.getPreCovidValue(), val.getPostCovidValue()));
				chart.add(obj);
			}
		}

		if (group.contentEquals("weekly")) {

			weeklabel = new ArrayList<>(new HashSet<>(weekLabel));
			for (int n : weeklabel) {
				labels.add("Week" + n);
			}

		}

		else if (group.contentEquals("monthly")) {
			String[] monthList = { "December", "January", "February", "March", "April", "May", "June", "July", "August",
					"September", "October", "November" };
			monthlabel = new ArrayList<>(new HashSet<>(monthLabel));
			for (String index : monthlabel) {
				int ind = (Integer.parseInt(index)) % 12;
				labels.add(monthList[ind]);
			}
		}

		else if (group.contentEquals("daily")) {
			daylabel = new ArrayList<>(new HashSet<>(dayLabel));
			Collections.sort(daylabel);
			for (int k = 0; k < daylabel.size(); k++) {
				Date nowDate = formatYMD.parse(daylabel.get(k));
				labels.add(formatDMY.format(nowDate));
			}
		}

		else if (group.contentEquals("covid")) {
			labels.add("Pre-COVID");
			labels.add("Post-COVID");
		}

		value.setLabels(labels);
		value.setDatasets(chart);
		return value;

	}

	public ChartObjectCustom getDataSector(List<String> sectorList, String startDate, String endDate, String type,
			String group, String boundaryDate) throws ParseException {

		Date sDate = formatYMD.parse(startDate);
		Date eDate = formatYMD.parse(endDate);
		int i = 120;
		ChartObjectCustom value = new ChartObjectCustom();
		List<ChartObject> chart = new ArrayList<>();
		List<String> monthLabel = new ArrayList<>();
		List<String> monthlabel = new ArrayList<>();
		List<String> labels = new ArrayList<>();
		List<String> dayLabel = new ArrayList<>();
		List<String> daylabel = new ArrayList<>();
		List<Stock> stocknew = new ArrayList<>();
		ArrayList<Double> valueList = new ArrayList<>();
		List<Integer> weekLabel = new ArrayList<>();
		List<Integer> weeklabel = new ArrayList<>();

		for (String sector : sectorList) {
			List<Company> company = getBySector(sector);
			for (Company comp : company) {
				List<Stock> stocks = comp.getStocks();
				for (Stock stock : stocks) {
					Date nDate = formatYMD.parse(stock.getDate());
					if (nDate.before(eDate) && nDate.after(sDate) || nDate.equals(sDate) || nDate.equals(eDate)) {
						stocknew.add(stock);
					}
				}
			}

			ChartObject obj = new ChartObject();
			obj.setLabel(sector);
			obj.setBackgroundColor(colorArray[i]);
			obj.setBorderColor(colorArray[i]);
			i--;

			if (group.contentEquals("daily")) {

				if (type.contentEquals("price")) {
					Map<String, Double> daily = stocknew.stream()
							.collect(Collectors.groupingBy(Stock::getDate, Collectors.averagingDouble(Stock::getClose)))
							.entrySet().stream().sorted(comparingByKey())
							.collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

					dayLabel = new ArrayList<>(daily.keySet());
					valueList = new ArrayList<>(daily.values());
				}

				else if (type.contentEquals("volume")) {
					Map<String, Double> daily = stocknew.stream()
							.collect(
									Collectors.groupingBy(Stock::getDate, Collectors.averagingDouble(Stock::getVolume)))
							.entrySet().stream().sorted(comparingByKey())
							.collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
					dayLabel = new ArrayList<>(daily.keySet());
					valueList = new ArrayList<>(daily.values());
				} else {
					logger.error("Incorrect parameters entered");
				}
				obj.setData(valueList);
				chart.add(obj);

			}

			else if (group.contentEquals("weekly")) {

				if (type.contentEquals("price")) {
					Map<Integer, Double> weekly = stocknew.stream()
							.collect(Collectors.groupingBy(Stock::getWeek, Collectors.averagingDouble(Stock::getClose)))
							.entrySet().stream().sorted(comparingByKey())
							.collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

					weekLabel = new ArrayList<>(weekly.keySet());
					valueList = new ArrayList<>(weekly.values());
				}

				else if (type.contentEquals("volume")) {
					Map<Integer, Double> weekly = stocknew.stream()
							.collect(
									Collectors.groupingBy(Stock::getWeek, Collectors.averagingDouble(Stock::getVolume)))
							.entrySet().stream().sorted(comparingByKey())
							.collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
					weekLabel = new ArrayList<>(weekly.keySet());
					valueList = new ArrayList<>(weekly.values());
				} else {
					logger.error("Incorrect parameters entered");
				}
				obj.setData(valueList);
				chart.add(obj);

			}

			else if (group.contentEquals("monthly")) {

				if (type.contentEquals("price")) {

					Map<String, Double> monthly = stocknew.stream()
							.collect(Collectors.groupingBy(Stock::getMonth, Collectors.averagingDouble(Stock::getClose)))
							.entrySet().stream().sorted(comparingByKey())
							.collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

					monthLabel = new ArrayList<>(monthly.keySet());
					valueList = new ArrayList<>(monthly.values());
				}

				else if (type.contentEquals("volume")) {
					Map<String, Double> monthly = stocknew.stream()
							.collect(Collectors.groupingBy(Stock::getMonth, Collectors.averagingDouble(Stock::getVolume)))
							.entrySet().stream().sorted(comparingByKey())
							.collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
					monthLabel = new ArrayList<>(monthly.keySet());
					valueList = new ArrayList<>(monthly.values());
				} else {
					logger.error("Incorrect parameters entered");
				}
				obj.setData(valueList);
				chart.add(obj);

			}

			else if (group.contentEquals("covid")) {
				AverageValues val = sectorAverage(sector, type, boundaryDate);
				obj.setData(Arrays.asList(val.getPreCovidValue(), val.getPostCovidValue()));
				chart.add(obj);

			}
		}

		if (group.contentEquals("weekly")) {

			weeklabel = new ArrayList<>(new HashSet<>(weekLabel));
			for (int n : weeklabel) {
				labels.add("Week" + n);
			}

		}

		else if (group.contentEquals("monthly")) {
			String[] monthList = { "December", "January", "February", "March", "April", "May", "June", "July", "August",
					"September", "October", "November" };
			monthlabel = new ArrayList<>(new HashSet<>(monthLabel));
			for (String index : monthlabel) {
				int ind = (Integer.parseInt(index)) % 12;
				labels.add(monthList[ind]);
			}
		}

		else if (group.contentEquals("daily")) {
			daylabel = new ArrayList<>(new HashSet<>(dayLabel));
			Collections.sort(daylabel);
			for (int k = 0; k < daylabel.size(); k++) {
				Date nowDate = formatYMD.parse(daylabel.get(k));
				labels.add(formatDMY.format(nowDate));
			}
		}

		else if (group.contentEquals("covid")) {
			labels.add("Pre-COVID");
			labels.add("Post-COVID");
		}

		value.setLabels(labels);
		value.setDatasets(chart);
		return value;

	}

	public ChartObjectCustom getChart(List<String> tickerList, List<String> sectorList, String startDate,
			String endDate, String type, String group, String option, String boundaryDate) throws ParseException {

		if (sectorList.isEmpty()) {
			return getDataCompany(tickerList, startDate, endDate, type, group, boundaryDate);
		}

		else if (tickerList.isEmpty()) {
			return getDataSector(sectorList, startDate, endDate, type, group, boundaryDate);
		}

		else if (option.contentEquals("company")) {
			List<String> tickerNew = new ArrayList<>();
			for (String ticker : tickerList) {

				Company company = getByTicker(ticker);
				if (sectorList.contains(company.getSector())) {
					tickerNew.add(ticker);
				}
			}

			return getDataCompany(tickerNew, startDate, endDate, type, group, boundaryDate);

		}

		else if (option.contentEquals("both")) {

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
			List<String> newSector = new ArrayList<>(new HashSet<String>(sectorNew));
			value1 = getDataCompany(tickerNew, startDate, endDate, type, group, boundaryDate);
			value2 = getDataSector(newSector, startDate, endDate, type, group, boundaryDate);
			List<ChartObject> obj1 = value1.getDatasets();
			List<ChartObject> obj2 = value2.getDatasets();
			obj1.addAll(obj2);
			value1.setDatasets(obj1);
			return value1;

		}

		else {
			return null;
		}

	}
}