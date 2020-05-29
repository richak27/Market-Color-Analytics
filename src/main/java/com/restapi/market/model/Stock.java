package com.restapi.market.model;

public class Stock {
	
	private String date;
	private float close;
	private float volume;
	private String period;
	
	public Stock() {
		super();
	}

	public Stock(String date, float close, float volume, String period) {
		super();
		this.date = date;
		this.close = close;
		this.volume = volume;
		this.period = period;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public float getClose() {
		return close;
	}

	public void setClose(float close) {
		this.close = close;
	}

	public float getVolume() {
		return volume;
	}

	public void setVolume(float volume) {
		this.volume = volume;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}
		

}
