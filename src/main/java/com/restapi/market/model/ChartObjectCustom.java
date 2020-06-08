package com.restapi.market.model;

import java.util.List;

public class ChartObjectCustom {
	
	private List<String>labels;
	private List<ChartObject>Datasets;
	
	
	public ChartObjectCustom() {}


	public ChartObjectCustom(List<String> labels, List<ChartObject> Datasets) {
		super();
		this.labels = labels;
		this.Datasets = Datasets;
	}


	public List<String> getLabels() {
		return labels;
	}


	public List<ChartObject> getDatasets() {
		return Datasets;
	}


	public void setLabels(List<String> labels) {
		this.labels = labels;
	}


	public void setDatasets(List<ChartObject> Datasets) {
		this.Datasets = Datasets;
	};

	

	
	

}
