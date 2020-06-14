package com.restapi.market.model;

import java.util.List;

public class ChartObject {
	
	private String label;
	private List<Double> data;
	private String backgroundColor;
	private String borderColor;
	private boolean fill;
	
	public ChartObject() {
		super();
	}

	public ChartObject(String label, List<Double> data, String backgroundColor, String borderColor, boolean fill) {
		super();
		this.label = label;
		this.data = data;
		this.backgroundColor = backgroundColor;
		this.borderColor = borderColor;
		this.fill = fill;
	}

	public String getLabel() {
		return label;
	}

	public List<Double> getData() {
		return data;
	}

	public String getBackgroundColor() {
		return backgroundColor;
	}

	public String getBorderColor() {
		return borderColor;
	}

	public boolean isFill() {
		return fill;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setData(List<Double> data) {
		this.data = data;
	}

	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public void setBorderColor(String borderColor) {
		this.borderColor = borderColor;
	}

	public void setFill() {
		this.fill = false;
	}
	
	
	

}
