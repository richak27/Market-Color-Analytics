package com.restapi.market.model;

import java.util.List;

public class ChartObjectCustom {
	
	private List<String>labels;
	private List<ChartObject>datasets;
	
	
	public ChartObjectCustom() {
		super();
	}


	public ChartObjectCustom(List<String> labels, List<ChartObject> datasets) {
		super();
		this.labels = labels;
		this.datasets = datasets;
	}


	public List<String> getLabels() {
		return labels;
	}


	public List<ChartObject> getDatasets() {
		return datasets;
	}


	public void setLabels(List<String> labels) {
		this.labels = labels;
	}


	public void setDatasets(List<ChartObject> datasets) {
		this.datasets = datasets;
	};

	

	
	

}
