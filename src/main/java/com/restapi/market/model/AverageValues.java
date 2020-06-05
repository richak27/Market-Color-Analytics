package com.restapi.market.model;

public class AverageValues {
	
	
	private double preCovidValue;
	private double postCovidValue;
	private double deviation;
	
	public AverageValues() {};
	
	
	public AverageValues(double preCovidValue, double postCovidValue, double deviation) {
		super();
		this.preCovidValue = preCovidValue;
		this.postCovidValue = postCovidValue;
		this.deviation = deviation;
	}
	public double getPreCovidValue() {
		return preCovidValue;
	}
	public double getPostCovidValue() {
		return postCovidValue;
	}
	public double getDeviation() {
		return deviation;
	}
	public void setPreCovidValue(double preCovidValue) {
		this.preCovidValue = preCovidValue;
	}
	public void setPostCovidValue(double postCovidValue) {
		this.postCovidValue = postCovidValue;
	}
	public void setDeviation(double deviation) {
		this.deviation = deviation;
	}
	
	

}
