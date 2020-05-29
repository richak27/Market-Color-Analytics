package com.restapi.market.model;

public class VolumeAverage {
	double preCovidVolume;
	double postCovidVolume;
	
	public VolumeAverage() {
		super();
	}

	public double getPreCovidVolume() {
		return preCovidVolume;
	}

	public double getPostCovidVolume() {
		return postCovidVolume;
	}

	public void setPreCovidVolume(double preCovidVolume) {
		this.preCovidVolume = preCovidVolume;
	}

	public void setPostCovidVolume(double postCovidVolume) {
		this.postCovidVolume = postCovidVolume;
	}
	
	
	

}