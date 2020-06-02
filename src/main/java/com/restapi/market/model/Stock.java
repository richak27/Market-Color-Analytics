package com.restapi.market.model;

public class Stock {
	private String date;
	private String period;
	private double close;
	private double volume;
	private String week;

	public Stock() {
		super();
	}

	public Stock(String date, String period, double close, double volume,String week) {
		super();
		this.date = date;
		this.period = period;
		this.close = close;
		this.volume = volume;
		this.week=week;
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
	
	public String getWeek()
	{
		return week;
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
	
	public void setWeek(String week) {
		this.week = week ;
	}

}
