package com.restapi.market.model;

public class Calculate {

	double price;
	double volume;
	
	public Calculate() {
		super();
	}
	
	public Calculate(double price, double volume) {
		super();
		this.price = price;
		this.volume = volume;
	}

	public double getPrice() {
		return price;
	}

	public double getVolume() {
		return volume;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public void setVolume(double volume) {
		this.volume = volume;
	}
		
}
