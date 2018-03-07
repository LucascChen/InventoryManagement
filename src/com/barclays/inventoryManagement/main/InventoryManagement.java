package com.barclays.inventoryManagement.main;

import java.io.File;
import java.util.Scanner;

import com.barclays.inventoryManagement.service.InventoryManagementService;

public class InventoryManagement {

	
	private static InventoryManagementService inventoryManagementService = InventoryManagementService.getInstance();
	
	public static void main(String[] args) {
		
		try {
            Scanner inputStream = new Scanner(new File("INVENTORY.txt"));
            while (inputStream.hasNext()) {
                String input = inputStream.nextLine();
                inventoryManagementService.operations(input);
            }
            inputStream.close();
        } catch (Exception e) {
        	System.out.println(e.getStackTrace());
            System.out.println("Invalid file...");
        }
		
	}

}
