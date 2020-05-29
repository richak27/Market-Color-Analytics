package com.restapi.market.model;

public class Average {
	 
	private float pre_covid;
	private float post_covid;
	
	
	public Average() {
		super();
	
	}


	public Average(float pre_covid, float post_covid) {
		super();
		this.pre_covid = pre_covid;
		this.post_covid = post_covid;
	}
	
	
	public float getPre_covid() {
		return pre_covid;
	}
	public void setPre_covid(float pre_covid) {
		this.pre_covid = pre_covid;
	}
	public float getPost_covid() {
		return post_covid;
	}
	public void setPost_covid(float post_covid) {
		this.post_covid = post_covid;
	}
	
	

}
