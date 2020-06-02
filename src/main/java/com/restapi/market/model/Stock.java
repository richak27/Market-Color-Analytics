package com.restapi.market.model;

public class Stock {
	private String date;
	private String period;
	private double close;
	private double volume;
	private String week;
	private String month;

	public Stock() {
		super();
	}

	public Stock(String date, String period, double close, double volume,String week,String month) {
		super();
		this.date = date;
		this.period = period;
		this.close = close;
		this.volume = volume;
		this.week=week;
		this.month=month;
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
	
	public String getMonth()
	{
		return month;
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

	public void setMonth(String month) {
		this.month = month ;
	}

}
