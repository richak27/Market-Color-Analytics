package com.restapi.market.model;

public class PriceAverage {
	float preCovidPrice;
	float postCovidPrice;
	public PriceAverage() {
		super();
	}
	public PriceAverage(float preCovidPrice, float postCovidPrice) {
		super();
		this.preCovidPrice = preCovidPrice;
		this.postCovidPrice = postCovidPrice;
	}
	public float getPreCovidPrice() {
		return preCovidPrice;
	}
	public void setPreCovidPrice(float preCovidPrice) {
		this.preCovidPrice = preCovidPrice;
	}
	public float getPostCovidPrice() {
		return postCovidPrice;
	}
	public void setPostCovidPrice(float postCovidPrice) {
		this.postCovidPrice = postCovidPrice;
	}
	
	
	
}
