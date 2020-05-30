package com.restapi.market.model;

public class PriceAverage {
	double preCovidPrice;
	double postCovidPrice;
	double deviationPrice;
	public PriceAverage() {
		super();
	}
	
	
	public PriceAverage(double preCovidPrice, double postCovidPrice, double deviationPrice) {
		super();
		this.preCovidPrice = preCovidPrice;
		this.postCovidPrice = postCovidPrice;
		this.deviationPrice = deviationPrice;
		
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
	public double getDeviationPrice() {
		return deviationPrice;
	}
	public void setDeviationPrice(double deviationPrice) {
		this.deviationPrice = deviationPrice;
	}	
}