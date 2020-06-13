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
	public String addStocksByTicker(String ticker) throws ParseException {
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
		return ticker + "information added to DB";
	}

	// seed database with data of all companies
	public String seedDb() {
		List<String> tickers = mongoTemplate.query(Company.class).distinct("ticker").as(String.class).all();
		for (String ticker : tickers) {
			try {
				addStocksByTicker(ticker);
			} catch (Exception exception) {
				System.out.print("Did not find " + ticker + " ");
				System.out.println(exception);
			}
		}
		return "Seeding Successful!";
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
				System.out.println(exception);
			}
		}
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

		volumeAverage.setPreCovidValue((preVolumeSum) / (sizeOfPre));
		volumeAverage.setPostCovidValue((postVolumeSum) / (stocks.size() - sizeOfPre));
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

		priceAverage.setPreCovidValue((preCloseSum) / (sizeOfPre));
		priceAverage.setPostCovidValue((postCloseSum) / (stocks.size() - sizeOfPre));
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

		priceAverage.setPreCovidValue((preCloseSum) / (company.size()));
		priceAverage.setPostCovidValue((postCloseSum) / (company.size()));
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

			postVolumeSum = postVolumeSum + calAvgPriceByCompany(comp.getTicker(), boundaryDate).getPostCovidValue();

		}

		volumeAverage.setPreCovidValue((preVolumeSum) / (company.size()));
		volumeAverage.setPostCovidValue((postVolumeSum) / (company.size()));
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
		Map<String, Double> values = new HashMap<String, Double>();

		for (String i : tickerList) {
			AverageValues priceAverage = calAvgPriceByCompany(i, boundaryDate);
			values.put(i, priceAverage.getDeviation());
		}

		return values.entrySet().stream().sorted(comparingByValue())
				.collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
	}

	// Sorted Deviation for Companies
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
		calc.setPrice(closeSum / stocks.size());
		calc.setVolume(volumeSum / stocks.size());
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
		Date eDate = formatYMD.parse(endDate);
		Date sDate = formatYMD.parse(startDate);
		for (Company comp : companies) {

			List<Stock> stocks = comp.getStocks();
			for (Stock stock : stocks) {

				String nDate = stock.getDate();
				Date nowDate = formatYMD.parse(nDate);
				if (nowDate.before(eDate) && nowDate.after(sDate) || nowDate.equals(sDate) || nowDate.equals(eDate)) {
					stocksnew.add(stock);
				}
			}
		}
		return averagestock(stocksnew);
	}

/////////////                              DAILY COMPANY                          ////////////-----p
	public Map<String, Double> dailyCompany(String ticker, String frdate, String todate, String type)
			throws ParseException {

		Date toDate = formatYMD.parse(todate);
		Date frDate = formatYMD.parse(frdate);
		Company company = getByTicker(ticker);
		List<Stock> stocknew = new ArrayList<>();

		List<Stock> stocks = company.getStocks();
		for (Stock stock : stocks) {

			Date nDate = formatYMD.parse(stock.getDate());
			if (nDate.before(toDate) && nDate.after(frDate) || nDate.equals(toDate) || nDate.equals(frDate)) {
				stocknew.add(stock);
			}
		}

		if (type.contentEquals("price")) {

			return stocknew.stream()
					.collect(Collectors.groupingBy(Stock::getDate, Collectors.averagingDouble(Stock::getClose)))
					.entrySet().stream().sorted(comparingByKey())
					.collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
		}

		else if (type.contentEquals("volume")) {
			return stocknew.stream()
					.collect(Collectors.groupingBy(Stock::getDate, Collectors.averagingDouble(Stock::getVolume)))
					.entrySet().stream().sorted(comparingByKey())
					.collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

		}

		else {

			System.out.print("Enter correct parameters");
			return null;
		}
	}

/////////////////                     DAILY SECTOR                     /////////////////----p
	public Map<String, Double> dailySector(String sector, String startdate, String enddate, String type)
			throws ParseException {

		List<Company> company = getBySector(sector);
		Date startDate = formatYMD.parse(startdate);
		Date endDate = formatYMD.parse(enddate);
		List<Stock> stocknew = new ArrayList<>();
		for (Company comp : company) {

			List<Stock> stocks = comp.getStocks();
			for (Stock stock : stocks) {

				Date nDate = formatYMD.parse(stock.getDate());
				if (nDate.before(endDate) && nDate.after(startDate) || nDate.equals(startDate)
						|| nDate.equals(endDate)) {
					stocknew.add(stock);
				}
			}

		}

		if (type.contentEquals("price")) {
			return stocknew.stream()
					.collect(Collectors.groupingBy(Stock::getDate, Collectors.averagingDouble(Stock::getClose)))
					.entrySet().stream().sorted(comparingByKey())
					.collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

		}

		else if (type.contentEquals("volume")) {
			return stocknew.stream()
					.collect(Collectors.groupingBy(Stock::getDate, Collectors.averagingDouble(Stock::getVolume)))
					.entrySet().stream().sorted(comparingByKey())
					.collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

		} else {

			System.out.print("Enter correct parameters");
			return null;
		}

	}

//////////////                      WEEKLY COMPANY              //////////////////////---p
	public Map<Integer, Double> weeklyCompany(String ticker, String startDate, String endDate, String type)
			throws ParseException {
		Company company = getByTicker(ticker);
		List<Stock> stocks = company.getStocks();
		List<Stock> stocksnew = new ArrayList<>();
		Date sDate = formatYMD.parse(startDate);
		Date eDate = formatYMD.parse(endDate);
		for (Stock stock : stocks) {
			Date nowDate = formatYMD.parse(stock.getDate());
			if ((nowDate.after(sDate) && nowDate.before(eDate)) || nowDate.equals(sDate) || nowDate.equals(eDate))
				stocksnew.add(stock);
		}

		if (type.contentEquals("price")) {
			return stocksnew.stream()
					.collect(Collectors.groupingBy(Stock::getWeek, Collectors.averagingDouble(Stock::getClose)))
					.entrySet().stream().sorted(comparingByKey())
					.collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

		}

		else if (type.contentEquals("volume")) {
			return stocksnew.stream()
					.collect(Collectors.groupingBy(Stock::getWeek, Collectors.averagingDouble(Stock::getVolume)))
					.entrySet().stream().sorted(comparingByKey())
					.collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

		}

		else {

			System.out.print("Enter correct parameters");
			return null;
		}

	}

/////////////////////                 MONTHLY COMPANY              ///////////////////////------p
	public Map<String, Double> monthlyCompany(String ticker, String startDate, String endDate, String type)
			throws ParseException {
		Company company = getByTicker(ticker);
		List<Stock> stocks = company.getStocks();
		List<Stock> stocksnew = new ArrayList<>();
		Date sDate = formatYMD.parse(startDate);
		Date eDate = formatYMD.parse(endDate);
		for (Stock stock : stocks) {
			Date nowDate = formatYMD.parse(stock.getDate());
			if ((nowDate.after(sDate) && nowDate.before(eDate)) || nowDate.equals(sDate) || nowDate.equals(eDate)) {
				stocksnew.add(stock);
			}
		}

		if (type.contentEquals("price")) {

			return stocksnew.stream()
					.collect(Collectors.groupingBy(Stock::getMonth, Collectors.averagingDouble(Stock::getClose)));

		}

		else if (type.contentEquals("volume")) {
			return stocksnew.stream()
					.collect(Collectors.groupingBy(Stock::getMonth, Collectors.averagingDouble(Stock::getVolume)));

		}

		else {

			System.out.print("Enter correct parameters");
			return null;
		}

	}
////////////////////////                WEEKLY SECTOR         ////////////////////////----------p

	public Map<Integer, Double> weeklySector(String sector, String startDate, String endDate, String type)
			throws ParseException {
		List<Company> companies = getBySector(sector);
		List<Stock> stocks = new ArrayList<>();
		for (Company company : companies) {
			stocks.addAll(company.getStocks());
		}
		List<Stock> stocksnew = new ArrayList<>();
		Date sDate = formatYMD.parse(startDate);
		Date eDate = formatYMD.parse(endDate);
		for (Stock stock : stocks) {
			Date nowDate = formatYMD.parse(stock.getDate());
			if ((nowDate.after(sDate) && nowDate.before(eDate)) || nowDate.equals(sDate) || nowDate.equals(eDate)) {
				stocksnew.add(stock);
			}
		}

		if (type.contentEquals("price")) {
			return stocksnew.stream()
					.collect(Collectors.groupingBy(Stock::getWeek, Collectors.averagingDouble(Stock::getClose)))
					.entrySet().stream().sorted(comparingByKey())
					.collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
		}

		else if (type.contentEquals("volume")) {
			return stocksnew.stream()
					.collect(Collectors.groupingBy(Stock::getWeek, Collectors.averagingDouble(Stock::getVolume)))
					.entrySet().stream().sorted(comparingByKey())
					.collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
		}

		else {
			System.out.print("Enter correct parameters");
			return null;
		}

	}

//////////////////////////////////              MONTHLY SECTOR          ////////////////////////////////////------p
	public Map<String, Double> monthlySector(String sector, String startDate, String endDate, String type)
			throws ParseException {
		List<Company> companies = getBySector(sector);
		List<Stock> stocks = new ArrayList<>();
		for (Company company : companies) {
			stocks.addAll(company.getStocks());
		}
		List<Stock> stocksnew = new ArrayList<>();
		Date sDate = formatYMD.parse(startDate);
		Date eDate = formatYMD.parse(endDate);
		for (Stock stock : stocks) {
			Date nowDate = formatYMD.parse(stock.getDate());
			if ((nowDate.after(sDate) && nowDate.before(eDate)) || nowDate.equals(sDate) || nowDate.equals(eDate)) {
				stocksnew.add(stock);
			}
		}

		if (type.contentEquals("price")) {

			return stocksnew.stream()
					.collect(Collectors.groupingBy(Stock::getMonth, Collectors.averagingDouble(Stock::getClose)));

		}

		else if (type.contentEquals("volume")) {
			return stocksnew.stream()
					.collect(Collectors.groupingBy(Stock::getMonth, Collectors.averagingDouble(Stock::getVolume)));
		}

		else {
			System.out.print("Enter correct parameters");
			return null;
		}

	}

///////////////////////    FUNCTION FOR DAILY WEEKLY MONTHLY FOR A SECTOR               ///////////////////////////----p
	public Map<String, Double> dataSector(String sector, String startDate, String endDate, String type, String range)
			throws ParseException {
		if (range.contentEquals("daily")) {
			return dailySector(sector, startDate, endDate, type);
		}

		/*
		 * else if (range.contentEquals("weekly")) { return weeklySector(sector,
		 * startDate, endDate, type); }
		 */

		else if (range.contentEquals("monthly")) {
			return monthlySector(sector, startDate, endDate, type);
		}

		else {

			System.out.print("Enter correct parameters");
			return null;
		}
	}

/////////////////////          FUNCTION FOR DAILY WEEKLY MONTHLY FOR A COMPANY           ////////////////////////////
	public Map<String, Double> dataCompany(String ticker, String startDate, String endDate, String type, String range)
			throws ParseException {
		if (range.contentEquals("daily")) {
			return dailyCompany(ticker, startDate, endDate, type);
		}

		/*
		 * else if (range.contentEquals("weekly")) { return WeeklyCompany(ticker,
		 * startDate, endDate, type); }
		 */
		else if (range.contentEquals("monthly")) {
			return monthlyCompany(ticker, startDate, endDate, type);
		}

		else {

			System.out.print("Enter correct parameters");
			return null;
		}
	}

	// List of objects with daily data for a company grid grid grid
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

	// List of objects with daily data for a sector
	public List<List<DailyData>> gridSector(String sector, String startDate, String endDate) throws ParseException {
		List<Company> companies = getBySector(sector);
		List<List<DailyData>> nestedList = new ArrayList<>();
		for (Company company : companies) {
			nestedList.add(gridCompany(company.getTicker(), startDate, endDate));
		}
		return nestedList;
	}

	// Companies with sector selected, returns only companies----p
	public Map<String, List<Double>> chartCompanySector(List<String> tickerList, List<String> sectorList, String type,
			String boundaryDate) throws ParseException {

		Map<String, List<Double>> dataMap = new HashMap<>();
		for (String ticker : tickerList) {
			Company company = getByTicker(ticker);
			if (sectorList.contains(company.getSector())) {

				AverageValues obj = companyAverage(ticker, type, boundaryDate);
				dataMap.put(company.getName(), Arrays.asList(obj.getPreCovidValue(), obj.getPostCovidValue()));
			}

		}

		return dataMap;
	}

	// Companies with sector selected, returns companies and avg values of
	// sectors----------p
	public Map<String, List<Double>> avgChartCompanySector(List<String> tickerList, List<String> sectorList,
			String type, String boundaryDate) throws ParseException {

		List<String> sectors = new ArrayList<>();

		Map<String, List<Double>> dataMap = new HashMap<>();
		for (String ticker : tickerList) {
			Company company = getByTicker(ticker);
			if (sectorList.contains(company.getSector())) {
				sectors.add(company.getSector());
				AverageValues obj = companyAverage(ticker, type, boundaryDate);
				dataMap.put(company.getName(), Arrays.asList(obj.getPreCovidValue(), obj.getPostCovidValue()));
			}

		}
		for (String sector : sectors) {
			AverageValues obj = sectorAverage(sector, type, boundaryDate);
			dataMap.put(sector, Arrays.asList(obj.getPreCovidValue(), obj.getPostCovidValue()));
		}
		return dataMap;
	}

	// Companies Selected return only companies-----p

	public Map<String, List<Double>> chartCompany(List<String> tickerList, String type, String boundaryDate)
			throws ParseException {
		Map<String, List<Double>> dataMap = new HashMap<>();
		for (String ticker : tickerList) {
			Company company = getByTicker(ticker);
			AverageValues obj = companyAverage(ticker, type, boundaryDate);
			dataMap.put(company.getName(), Arrays.asList(obj.getPreCovidValue(), obj.getPostCovidValue()));
		}
		return dataMap;
	}

	// Sector Selected return only Avg values of sectors------p

	public Map<String, List<Double>> chartSector(List<String> sectorList, String type, String boundaryDate)
			throws ParseException {
		Map<String, List<Double>> dataMap = new HashMap<>();
		for (String sector : sectorList) {
			AverageValues obj = sectorAverage(sector, type, boundaryDate);
			dataMap.put(sector, Arrays.asList(obj.getPreCovidValue(), obj.getPostCovidValue()));
		}
		return dataMap;
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
					filteredTickers.add(company.getTicker());
				}
			}
			for (String ticker : filteredTickers) {
				allCompanies.addAll(gridCompany(ticker, startDate, endDate));
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

	private String[] colorArray = { "#FFCDD2", "#F8BBD0", "#E1BEE7", "#D1C4E9", "#C5CAE9", "#B3E5FC", "#B2DFDB",
			"#FFECB3", "#FFCCBC", "#D7CCC8", "#F06292", "#64B5F6", "#FFCA28", "#8BC34A", "#A1887F", "#B71C1C",
			"#4A148C", "#CD5C5C", "#EC407A", "#7CB342", "#9CCC65", "#F08080", "#808080", "#000080",
			"#FF00FF", "#800080", "#00FFFF", "#008000", "#FFFF00", "#800000", "#FFC0CB", "#CD5C5C", "#F08080",
			"#FA8072", "#E9967A", "#FFA07A", "#DC143C", "#FF0000", "#B22222", "#8B0000", "#FFC0CB", "#FFB6C1",
			"#FF69B4", "#FF1493", "#C71585", "#DB7093", "#FF7F50", "#FF6347", "#FF4500", "#FF8C00",
			"#FFA500", "#FF69B4", "#FFA500", "#9400D3", "#7CFC00", "#2E8B57", "#191970", "#CD853F", "#800000",
			"#00FFFF", "#4682B4", "#00BFFF", "#4169E1", "#F4A460" };

	// ONLY COMPANIES (PRE POST)

	public List<ChartObject> getChartCompany(List<String> tickerList, String type, String boundaryDate)
			throws ParseException {
		int i = 0;
		List<ChartObject> chart = new ArrayList<>();

		for (String ticker : tickerList) {
			Company company = getByTicker(ticker);
			AverageValues obj = companyAverage(ticker, type, boundaryDate);

			ChartObject object = new ChartObject();
			
			object.setLabel(company.getName());
			object.setBorderColor(colorArray[i]);
			object.setBackgroundColor(colorArray[i]);
			object.setData(Arrays.asList(obj.getPreCovidValue(), obj.getPostCovidValue()));

			chart.add(object);
			i++;
		}

		return chart;
	}

	// COMPANIES WITH SECTOR, RETURN ONLY COMPANIES(PRE POST)
	public List<ChartObject> getChartCompanySector(List<String> tickerList, List<String> sectorList, String type,
			String boundaryDate) throws ParseException {

		List<ChartObject> chart = new ArrayList<>();
		int i = 0;
		for (String ticker : tickerList) {
			Company company = getByTicker(ticker);

			if (sectorList.contains(company.getSector())) {
				AverageValues obj = companyAverage(ticker, type, boundaryDate);

				ChartObject object = new ChartObject();
				
				object.setLabel(company.getName());
				object.setBorderColor(colorArray[i]);
				object.setBackgroundColor(colorArray[i]);
				object.setData(Arrays.asList(obj.getPreCovidValue(), obj.getPostCovidValue()));

				chart.add(object);
				i++;
			}

		}
		return chart;
	}

	// COMPANIES WITH SECTOR, RETURN COMPANIES AND SECTORS (PRE POST)
	public List<ChartObject> getAvgChartCompanySector(List<String> tickerList, List<String> sectorList, String type,
			String boundaryDate) throws ParseException {
		int i = 0;
		List<ChartObject> chart = new ArrayList<>();
		Set<String> sectors = new HashSet<>();
		for (String ticker : tickerList) {
			Company company = getByTicker(ticker);

			if (sectorList.contains(company.getSector())) {
				AverageValues obj = companyAverage(ticker, type, boundaryDate);
				sectors.add(company.getSector());
				ChartObject object = new ChartObject();
				
				object.setLabel(company.getName());
				object.setBorderColor(colorArray[i]);
				object.setBackgroundColor(colorArray[i]);
				object.setData(Arrays.asList(obj.getPreCovidValue(), obj.getPostCovidValue()));

				chart.add(object);
				i++;
			}

		}

		
		for (String sector : sectors) {
			AverageValues obj = sectorAverage(sector, type, boundaryDate);

			ChartObject object = new ChartObject();
			
			object.setLabel(sector);
			object.setBorderColor(colorArray[i]);
			object.setBackgroundColor(colorArray[i]);
			object.setData(Arrays.asList(obj.getPreCovidValue(), obj.getPostCovidValue()));

			chart.add(object);
			i++;
		}
		return chart;
	}

	// ONLY SECTORS PRE POST
	public List<ChartObject> getChartSector(List<String> sectorList, String type, String boundaryDate)
			throws ParseException {

		List<ChartObject> chart = new ArrayList<>();
		int i = 0;
		for (String sector : sectorList) {

			AverageValues obj = sectorAverage(sector, type, boundaryDate);

			ChartObject object = new ChartObject();
			
			object.setLabel(sector);
			object.setBorderColor(colorArray[i]);
			object.setBackgroundColor(colorArray[i]);
			object.setData(Arrays.asList(obj.getPreCovidValue(), obj.getPostCovidValue()));

			chart.add(object);
			i++;
		}

		return chart;
	}

	// ONLY COMPANIES (WEEKLY)

	public ChartObjectCustom weeklyCompanyObject(List<String> tickerList, String startDate, String endDate, String type)
			throws ParseException {
		int i = 0;
		ChartObjectCustom value = new ChartObjectCustom();
		List<ChartObject> chart = new ArrayList<>();
		List<String> labels = new ArrayList<>();
		List<Integer> keys = new ArrayList<>();

		for (String ticker : tickerList) {
			
			Company company = getByTicker(ticker);
			Map<Integer, Double> obj = weeklyCompany(ticker, startDate, endDate, type);
			if (i == 0)
				keys = new ArrayList<Integer>(obj.keySet());

			List<Double> valueList = new ArrayList<Double>(obj.values());
			ChartObject object = new ChartObject();

			object.setLabel(company.getName());
			object.setBorderColor(colorArray[i]);
			object.setBackgroundColor(colorArray[i]);
			object.setData(valueList);
			chart.add(object);
			i++;

		}
		for (int k = 0; k < keys.size(); k++)
			labels.add("week" + Integer.toString(keys.get(k)));
		value.setLabels(labels);
		value.setDatasets(chart);

		return value;
	}

	// COMPANIES WITH SECTOR, RETURN ONLY COMPANIES(WEEKLY)

	public ChartObjectCustom weeklyCompanySectorObject(List<String> tickerList, List<String> sectorList,
			String startDate, String endDate, String type) throws ParseException {

		ChartObjectCustom value = new ChartObjectCustom();
		List<ChartObject> chart = new ArrayList<>();
		List<String> labels = new ArrayList<>();
		List<Integer> keys = new ArrayList<>();

		int i = 0;
		for (String ticker : tickerList) {
			
			Company company = getByTicker(ticker);

			if (sectorList.contains(company.getSector())) {
				Map<Integer, Double> obj = weeklyCompany(ticker, startDate, endDate, type);
				if (i == 0)
					keys = new ArrayList<Integer>(obj.keySet());

				List<Double> valueList = new ArrayList<Double>(obj.values());
				ChartObject object = new ChartObject();

				object.setLabel(company.getName());
				object.setBorderColor(colorArray[i]);
				object.setBackgroundColor(colorArray[i]);
				object.setData(valueList);
				chart.add(object);
				i++;
			}

		}

		for (int k = 0; k < keys.size(); k++)
			labels.add("week" + Integer.toString(keys.get(k)));

		value.setLabels(labels);
		value.setDatasets(chart);

		return value;

	}

	// COMPANIES WITH SECTOR, RETURN COMPANIES AND SECTORS (WEEKLY)
	public ChartObjectCustom weeklyAvgCompanySectorObject(List<String> tickerList, List<String> sectorList,
			String startDate, String endDate, String type) throws ParseException {
		int i = 0;
		Set<String> sectors = new HashSet<>();
		ChartObjectCustom value = new ChartObjectCustom();
		List<ChartObject> chart = new ArrayList<>();
		List<String> labels = new ArrayList<>();
		List<Integer> keys = new ArrayList<>();

		for (String ticker : tickerList) {
			Company company = getByTicker(ticker);
			if (sectorList.contains(company.getSector())) {
				
				sectors.add(company.getSector());
				Map<Integer, Double> obj = weeklyCompany(ticker, startDate, endDate, type);
				if (i == 0)
					keys = new ArrayList<Integer>(obj.keySet());

				List<Double> valueList = new ArrayList<>(obj.values());
				ChartObject object = new ChartObject();

				object.setLabel(company.getName());
				object.setBorderColor(colorArray[i]);
				object.setBackgroundColor(colorArray[i]);
				object.setData(valueList);
				chart.add(object);
				i++;

			}
		}

		
		for (String sector : sectors) {
			
			Map<Integer, Double> obj = weeklySector(sector, startDate, endDate, type);
			List<Double> valueList = new ArrayList<>(obj.values());
			ChartObject object = new ChartObject();
			object.setLabel(sector);
			object.setBorderColor(colorArray[i]);
			object.setBackgroundColor(colorArray[i]);
			object.setData(valueList);
			chart.add(object);
			i++;
		}

		for (int k = 0; k < keys.size(); k++)
			labels.add("week" + Integer.toString(keys.get(k)));

		value.setLabels(labels);
		value.setDatasets(chart);

		return value;

	}

	// ONLY SECTORS (WEEKLY)
	public ChartObjectCustom weeklySectorObject(List<String> sectorList, String startDate, String endDate, String type)
			throws ParseException {

		ChartObjectCustom value = new ChartObjectCustom();
		List<ChartObject> chart = new ArrayList<>();
		List<String> labels = new ArrayList<>();
		List<Integer> keys = new ArrayList<>();
		int i = 0;
		for (String sector : sectorList) {
			
			Map<Integer, Double> obj = weeklySector(sector, startDate, endDate, type);
			if (i == 0)
				keys = new ArrayList<>(obj.keySet());

			List<Double> valueList = new ArrayList<>(obj.values());
			ChartObject object = new ChartObject();
			object.setLabel(sector);
			object.setBorderColor(colorArray[i]);
			object.setBackgroundColor(colorArray[i]);
			object.setData(valueList);
			chart.add(object);
			i++;
		}

		for (int k = 0; k < keys.size(); k++)
			labels.add("week" + Integer.toString(keys.get(k)));
		value.setLabels(labels);
		value.setDatasets(chart);

		return value;

	}

	/////// DAILY 1. ONLY COMPANIES
	public ChartObjectCustom dailyCompanyObject(List<String> tickerList, String startDate, String endDate, String type)
			throws ParseException {

		Date sDate = formatYMD.parse(startDate);
		Date eDate = formatYMD.parse(endDate);
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

				Date nDate = formatYMD.parse(stock.getDate());
				if (nDate.before(eDate) && nDate.after(sDate) || nDate.equals(sDate) || nDate.equals(eDate)) {
					stocknew.add(stock);
				}
			}

			ChartObject obj = new ChartObject();
			i++;
			obj.setLabel(company.getName());
			obj.setBackgroundColor(colorArray[i]);
			obj.setBorderColor(colorArray[i]);

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
		for (int k = 0; k < labels.size(); k++) {
			Date nowDate = formatYMD.parse(labels.get(k));
			labels.set(k, formatDMY.format(nowDate));
		}
		value.setLabels(labels);

		return value;
	}

/////// 	DAILY 2. ONLY SECTORS
	public ChartObjectCustom dailySectorObject(List<String> sectorList, String startDate, String endDate, String type)
			throws ParseException {

		Date sDate = formatYMD.parse(startDate);
		Date eDate = formatYMD.parse(endDate);
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
					Date nDate = formatYMD.parse(stock.getDate());
					if (nDate.before(eDate) && nDate.after(sDate) || nDate.equals(sDate) || nDate.equals(eDate)) {
						stocknew.add(stock);
					}
				}
			}

			ChartObject obj = new ChartObject();
			i--;
			obj.setLabel(sector);
			obj.setBackgroundColor(colorArray[i]);
			obj.setBorderColor(colorArray[i]);
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
		for (int k = 0; k < labels.size(); k++) {
			Date nowDate = formatYMD.parse(labels.get(k));
			labels.set(k, formatDMY.format(nowDate));
		}
		value.setLabels(labels);

		return value;
	}

	//////////// DAILY 3. COMPANY AND SECTOR RETURN MATCHED COMPANIES

	public ChartObjectCustom dailyCompanySectorObject(List<String> tickerList, List<String> sectorList,
			String startDate, String endDate, String type) throws ParseException {

		List<String> tickerNew = new ArrayList<>();
		ChartObjectCustom value = new ChartObjectCustom();
		for (String ticker : tickerList) {

			Company company = getByTicker(ticker);
			if (sectorList.contains(company.getSector())) {
				tickerNew.add(ticker);

			}
		}
		value = dailyCompanyObject(tickerNew, startDate, endDate, type);
		return value;
	}

	//////////// DAILY 4. COMPANY AND SECTOR RETURN MATCHED COMPANIES AND SECTORS

	public ChartObjectCustom dailyAvgCompanySectorObject(List<String> tickerList, List<String> sectorList,
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
		value1 = dailyCompanyObject(tickerNew, startDate, endDate, type);
		value2 = dailySectorObject(SectorNew, startDate, endDate, type);
		List<ChartObject> obj1 = value1.getDatasets();
		List<ChartObject> obj2 = value2.getDatasets();
		obj1.addAll(obj2);
		value1.setDatasets(obj1);
		return value1;
	}

	/////// MONTHLY 1. ONLY COMPANIES
	public ChartObjectCustom monthlyCompanyObject(List<String> tickerList, String startDate, String endDate,
			String type) throws ParseException {

		String[] monthList = { "December", "January", "February", "March", "April", "May", "June", "July", "August",
				"September", "October", "November", "December" };
		Date sDate = formatYMD.parse(startDate);
		Date eDate = formatYMD.parse(endDate);
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

				Date nDate = formatYMD.parse(stock.getDate());
				if (nDate.before(eDate) && nDate.after(sDate) || nDate.equals(sDate) || nDate.equals(eDate)) {
					stocknew.add(stock);
				}
			}

			ChartObject obj = new ChartObject();
			i++;
			obj.setLabel(company.getName());
			obj.setBackgroundColor(colorArray[i]);
			obj.setBorderColor(colorArray[i]);

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
	public ChartObjectCustom monthlySectorObject(List<String> sectorList, String startDate, String endDate, String type)
			throws ParseException {

		String[] monthList = { "December", "January", "February", "March", "April", "May", "June", "July", "August",
				"September", "October", "November", "December" };
		Date sDate = formatYMD.parse(startDate);
		Date eDate = formatYMD.parse(endDate);
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
					Date nDate = formatYMD.parse(stock.getDate());
					if (nDate.before(eDate) && nDate.after(sDate) || nDate.equals(sDate) || nDate.equals(eDate)) {
						stocknew.add(stock);
					}
				}
			}

			ChartObject obj = new ChartObject();
			i--;
			obj.setLabel(sector);
			obj.setBackgroundColor(colorArray[i]);
			obj.setBorderColor(colorArray[i]);
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

	public ChartObjectCustom monthlyCompanySectorObject(List<String> tickerList, List<String> sectorList,
			String startDate, String endDate, String type) throws ParseException {

		List<String> tickerNew = new ArrayList<>();
		ChartObjectCustom value = new ChartObjectCustom();
		for (String ticker : tickerList) {

			Company company = getByTicker(ticker);
			if (sectorList.contains(company.getSector())) {
				tickerNew.add(ticker);

			}
		}
		value = monthlyCompanyObject(tickerNew, startDate, endDate, type);
		return value;
	}

//////////// MONTHLY 4. COMPANY AND SECTOR RETURN MATCHED COMPANIES AND SECTORS	

	public ChartObjectCustom monthlyAvgCompanySectorObject(List<String> tickerList, List<String> sectorList,
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
		value1 = monthlyCompanyObject(tickerNew, startDate, endDate, type);
		value2 = monthlySectorObject(SectorNew, startDate, endDate, type);
		List<ChartObject> obj1 = value1.getDatasets();
		List<ChartObject> obj2 = value2.getDatasets();
		obj1.addAll(obj2);
		value1.setDatasets(obj1);
		return value1;
	}

}
