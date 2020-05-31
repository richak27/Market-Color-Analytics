package com.restapi.market.model;

public class VolumeAverage {
	double preCovidVolume;
	double postCovidVolume;
	double deviationVolume;

	public VolumeAverage() {
		super();
	}

	public VolumeAverage(double preCovidVolume, double postCovidVolume, double deviationVolume) {
		super();
		this.preCovidVolume = preCovidVolume;
		this.postCovidVolume = postCovidVolume;
		this.deviationVolume = deviationVolume;
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

	public double getDeviationVolume() {
		return deviationVolume;
	}

	public void setDeviationVolume(double deviationVolume) {
		this.deviationVolume = deviationVolume;
	}

}