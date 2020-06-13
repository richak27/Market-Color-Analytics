package com.restapi.market.model;

public class DailyData implements Comparable<DailyData> {

	public DailyData(String companyName, String sector, String ticker, String price, String volume, String date) {
		super();
		this.companyName = companyName;
		this.sector = sector;
		this.ticker = ticker;
		this.price = price;
		this.volume = volume;
		this.date = date;
	}

	public DailyData() {
		super();
	}

	private String companyName;
	private String sector;
	private String ticker;
	private String price;
	private String volume;
	private String date;

	public String getPrice() {
		return price;
	}

	public String getVolume() {
		return volume;
	}

	public String getDate() {
		return date;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public void setVolume(String volume) {
		this.volume = volume;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getCompanyName() {
		return companyName;
	}

	public String getSector() {
		return sector;
	}

	public String getTicker() {
		return ticker;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public void setSector(String sector) {
		this.sector = sector;
	}

	public void setTicker(String ticker) {
		this.ticker = ticker;
	}

	

	@Override
	public int compareTo(DailyData o) {
		return this.getDate().compareTo(o.getDate());
	}

}
