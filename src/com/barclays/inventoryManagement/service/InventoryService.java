package com.barclays.inventoryManagement.service;

import java.util.HashMap;
import java.util.Map;

import com.barclays.inventoryManagement.model.Product;
import com.barclays.inventoryManagement.model.Report;

public class InventoryService {

	private static InventoryService inventoryService;
	private static Map<String, Product> products;
	private static Report report;
	
	private InventoryService() {}
	
	public static InventoryService getInstance() {
		if (inventoryService == null) {
			inventoryService = new InventoryService();
		} 
		return inventoryService;
	}
	
	static {
        products = new HashMap<>();
        report = new Report();
        report.setAvailableProducts(new HashMap<>());
        report.setDeletedPrice(0d);
	}

	public static Map<String, Product> getProducts() {
		return products;
	}


	public static Report getReport() {
		return report;
	}


	
	
}
