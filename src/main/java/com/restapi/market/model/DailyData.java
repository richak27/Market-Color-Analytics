package com.restapi.market.model;

public class DailyData {
	
	public DailyData(String companyName, String sector, String ticker, double price, double volume, String date) {
		super();
		this.companyName = companyName;
		this.sector = sector;
		this.ticker = ticker;
		this.price = price;
		this.volume = volume;
		this.date = date;
	}
	
	
	public DailyData() {};
	
	private String companyName;
	private String sector;
	private String ticker;
	private double price;
	private double volume;
	private String date;
	
	
	public double getPrice() {
		return price;
	}
	public double getVolume() {
		return volume;
	}
	public String getDate() {
		return date;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public void setVolume(double volume) {
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

}