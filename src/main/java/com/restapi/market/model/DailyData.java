package com.restapi.market.model;

public class DailyData {
	
	
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
	
	public DailyData() {};
	
	public DailyData(double price, double volume, String date) {
		super();
		this.price = price;
		this.volume = volume;
		this.date = date;
	}

}