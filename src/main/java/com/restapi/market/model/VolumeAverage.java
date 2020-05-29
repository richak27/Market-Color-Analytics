package com.restapi.market.model;

public class VolumeAverage {
	float preCovidVolume;
	float postCovidVolume;
	
	public VolumeAverage() {
		super();
	}
	
	public VolumeAverage(float preCovidVolume, float postCovidVolume) {
		super();
		this.preCovidVolume = preCovidVolume;
		this.postCovidVolume = postCovidVolume;
	}

	public float getPreCovidVolume() {
		return preCovidVolume;
	}

	public void setPreCovidVolume(float preCovidVolume) {
		this.preCovidVolume = preCovidVolume;
	}

	public float getPostCovidVolume() {
		return postCovidVolume;
	}

	public void setPostCovidVolume(float postCovidVolume) {
		this.postCovidVolume = postCovidVolume;
	}
	
	

}
