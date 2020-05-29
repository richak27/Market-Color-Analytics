package com.restapi.market.model;

public class PriceAverage {
	double preCovidPrice;
	double postCovidPrice;
	public PriceAverage() {
		super();
	}
	
	
	public PriceAverage(double preCovidPrice, double postCovidPrice) {
		super();
		this.preCovidPrice = preCovidPrice;
		this.postCovidPrice = postCovidPrice;
	}


	public double getPreCovidPrice() {
		return preCovidPrice;
	}
	public double getPostCovidPrice() {
		return postCovidPrice;
	}
	public void setPreCovidPrice(double preCovidPrice) {
		this.preCovidPrice = preCovidPrice;
	}
	public void setPostCovidPrice(double postCovidPrice) {
		this.postCovidPrice = postCovidPrice;
	}
	
	
	
	
	
}