package com.restapi.market.model;

public class Stock {
	private String date;
	private String period;
	private String month;
	private String week;
	private double close;
	private double volume;

	public Stock() {
		super();
	}

	public Stock(String date, String period, String month, String week, double close, double volume) {
		super();
		this.date = date;
		this.period = period;
		this.month = month;
		this.week = week;
		this.close = close;
		this.volume = volume;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getWeek() {
		return week;
	}

	public void setWeek(String week) {
		this.week = week;
	}

	public double getClose() {
		return close;
	}

	public void setClose(double close) {
		this.close = close;
	}

	public double getVolume() {
		return volume;
	}

	public void setVolume(double volume) {
		this.volume = volume;
	}

}
