package com.restapi.market.model;

public class Stock {
	private String date;
	private String period;
	private double close;
	private double volume;
	
	public Stock() {
		super();
	}

	public Stock(String date, String period, double close, double volume) {
		super();
		this.date = date;
		this.period = period;
		this.close = close;
		this.volume = volume;
	}

	public String getDate() {
		return date;
	}

	public String getPeriod() {
		return period;
	}

	public double getClose() {
		return close;
	}

	public double getVolume() {
		return volume;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public void setClose(double close) {
		this.close = close;
	}

	public void setVolume(double volume) {
		this.volume = volume;
	}

	

}
