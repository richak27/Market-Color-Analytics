package com.restapi.market.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Companies")
public class Company {
	@Id
	private String id;
	private String name;
	private String ticker;
	private String sector;
	private List<Stock> stocks;
		
	public Company() {
		this.stocks = new ArrayList<>();
	}

	public Company(String id, String name, String ticker, String sector, List<Stock> stocks) {
		this.id = id;
		this.name = name;
		this.ticker = ticker;
		this.sector = sector;
		this.stocks = stocks;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTicker() {
		return ticker;
	}

	public void setTicker(String ticker) {
		this.ticker = ticker;
	}

	public String getSector() {
		return sector;
	}

	public void setSector(String sector) {
		this.sector = sector;
	}

	public List<Stock> getStocks() {
		return stocks;
	}

	public void setStocks(List<Stock> stocks) {
		this.stocks = stocks;
	}	

}
