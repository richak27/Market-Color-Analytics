package com.restapi.market.model;

import java.util.List;

public class ChartObjectCustom {
	
	private List<String>labels;
	private List<ChartObject>objectList;
	
	
	public ChartObjectCustom() {}


	public ChartObjectCustom(List<String> labels, List<ChartObject> objectList) {
		super();
		this.labels = labels;
		this.objectList = objectList;
	}


	public List<String> getLabels() {
		return labels;
	}


	public List<ChartObject> getObjectList() {
		return objectList;
	}


	public void setLabels(List<String> labels) {
		this.labels = labels;
	}


	public void setObjectList(List<ChartObject> objectList) {
		this.objectList = objectList;
	};

	

	
	

}
