package com.restapi.market.model;

public class Stock {
	private String date;
	private double close;
	private double volume;
	private int week;
	private String month;

	public Stock() {
		super();
	}

	public Stock(String date, double close, double volume,int week,String month) {
		super();
		this.date = date;
		this.close = close;
		this.volume = volume;
		this.week=week;
		this.month=month;
	}

	public String getDate() {
		return date;
	}

	

	public double getClose() {
		return close;
	}

	public double getVolume() {
		return volume;
	}
	
	public int getWeek()
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

	

	public void setClose(double close) {
		this.close = close;
	}

	public void setVolume(double volume) {
		this.volume = volume;
	}
	
	public void setWeek(int week) {
		this.week = week ;
	}

	public void setMonth(String month) {
		this.month = month ;
	}

}
