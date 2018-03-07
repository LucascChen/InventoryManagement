package com.barclays.inventoryManagement.model;

import java.util.Map;

public class Report {

	private Map<String, Integer> availableProducts;
    private Double deletedPrice;
    
    
	public Map<String, Integer> getAvailableProducts() {
		return availableProducts;
	}
	public void setAvailableProducts(Map<String, Integer> availableProducts) {
		this.availableProducts = availableProducts;
	}
	public Double getDeletedPrice() {
		return deletedPrice;
	}
	public void setDeletedPrice(Double deletedPrice) {
		this.deletedPrice = deletedPrice;
	}
    
    
}
