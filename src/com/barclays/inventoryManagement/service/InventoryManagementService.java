package com.barclays.inventoryManagement.service;

import java.time.LocalDateTime;
import java.util.SortedSet;
import java.util.TreeSet;

import com.barclays.inventoryManagement.model.Product;
import com.barclays.inventoryManagement.model.Trade;
import com.barclays.inventoryManagement.util.Status;

public class InventoryManagementService {
	
	public static InventoryManagementService inventoryManagementService;
	private InventoryService inventoryService = InventoryService.getInstance();
	
	private InventoryManagementService() {}
	
	public static InventoryManagementService getInstance() {
        if (inventoryManagementService == null) {
            inventoryManagementService = new InventoryManagementService();
        }
        return inventoryManagementService;
    }
	
	public void operations(String input) {
		String[] parsedInputArray = input.split(" ");
		if (parsedInputArray != null && parsedInputArray.length > 0) {
			String operation = parsedInputArray[0];
			if (operation == null) {
				System.out.println("Invalid input");
			}
			String productName = "";
			if (!operation.equals("report") && !operation.equals("#")) {
				productName = parsedInputArray[1];
			}
			switch (operation) {
				case "create":
					if (parsedInputArray.length == 4) {
						if (validateProduct(productName)) {
							if (InventoryService.getProducts().get(productName).getStatus() == Status.CURRENT) {
								System.out.println("already exist this product");
							} else {
								createProduct(parsedInputArray);
							}
						} else {
							createProduct(parsedInputArray);
						}
					} else {
						System.out.println("Invalid input...");
					}
					break;
				case "updateBuy":
					updateProduct(parsedInputArray, productName, true);
					break;
				case "updateSell":
					updateProduct(parsedInputArray, productName, false);
					break;
				case "delete":
					if (validateProduct(productName)) {
						inventoryService.getReport().getAvailableProducts().remove(productName);
						Product deletedProduct = inventoryService.getProducts().get(productName);
						inventoryService.getReport().setDeletedPrice(inventoryService.getReport().getDeletedPrice()
								+ deletedProduct.getTrade().getBuyAt() * deletedProduct.getQuantity());
						inventoryService.getProducts().get(productName).setStatus(Status.INACTIVE);
					} else {
						System.out.println("Invalid product...");
					}
					break;
				case "updateSellPrice":
					if (parsedInputArray.length == 3) {
						if (inventoryService.getProducts().containsKey(productName)) {
							updateSalePrice(parsedInputArray);
						}
					} else {
						System.out.println("Invalid update sale price...");
					}
					break;
				case "report":
					System.out.println("\tInventory Report");
					System.out.println("Item Name\tBought At\tSold At\tAvailableQty\tValue");
					
					Double total = 0d;
					Double profit = 0d;
					SortedSet<String> keys = new TreeSet<String>(inventoryService.getReport().getAvailableProducts().keySet());
					for (String prodName: keys) {
						Product inventoryProduct = inventoryService.getProducts().get(prodName);
						if (inventoryProduct.getStatus() != Status.INACTIVE) {
							Integer initQuantity = inventoryService.getReport().getAvailableProducts().get(prodName);
							Double currentValue = initQuantity * inventoryProduct.getTrade().getBuyAt() 
													- inventoryProduct.getTrade().getBuyAt() * (initQuantity - inventoryProduct.getQuantity());
							if (inventoryProduct.getStatus() == Status.CURRENT) {
								total += currentValue;
							}
							profit += (inventoryProduct.getTrade().getSellAt() - 
												inventoryProduct.getTrade().getBuyAt()) * (initQuantity - inventoryProduct.getQuantity());
							System.out.println(inventoryProduct.getProductName() + "\t" + inventoryProduct.getTrade().getBuyAt() + "\t" + 
													inventoryProduct.getTrade().getSellAt() + "\t" + inventoryProduct.getQuantity() + "\t" + 
													currentValue);
						}
					}
					System.out.println("Total Value\t\t\t\t\t" + total);
                    System.out.println("Profit since previous report\t" + (profit - InventoryService.getReport().getDeletedPrice()));
                    InventoryService.getProducts().forEach((prodName, i) -> {
                        if (InventoryService.getProducts().get(prodName).getStatus() == Status.CURRENT) {
                        	InventoryService.getReport().getAvailableProducts().put(prodName, InventoryService.getProducts().get(prodName).getQuantity());
                        }
                    });
                    InventoryService.getReport().setDeletedPrice(0d);
                    
                    break;
				case "#":
					System.out.println("End of Input..");
					break;
				default:
					System.out.println("No this operations");
			}
		}
	}
	
	private void createProduct(String[] parsedInputArray) {
		try {
			Product product = new Product();
			product.setProductName(parsedInputArray[1]);
			Trade trade = new Trade();
			trade.setBuyAt(Double.parseDouble(parsedInputArray[2]));
			trade.setSellAt(Double.parseDouble(parsedInputArray[3]));
			product.setTrade(trade);
			product.setQuantity(0);
			product.setStatus(Status.CURRENT);
			product.setCreatedTime(LocalDateTime.now());
			product.setUpdatedTime(LocalDateTime.now());
			InventoryService.getProducts().put(parsedInputArray[1], product);
			InventoryService.getReport().getAvailableProducts().put(parsedInputArray[1], 0);
			
		} catch (NumberFormatException e) {
			System.out.println("Invalid Price value! Please check input..");
		}
	}
	
	private Boolean updateProduct(String[] parsedInputArray, String productName, Boolean isBuy) {
		if (parsedInputArray.length == 3) {
			if (validateProduct(productName)) {
				Integer quantity = validateQuantity(parsedInputArray[2]);
				if(quantity != null) {
					if (!isBuy && inventoryService.getProducts().get(productName).getQuantity() < quantity) {
						System.out.println("update quantity is invalid");
					} else {
						Integer updateQuantity = isBuy ? (Math.addExact(inventoryService.getProducts().get(productName).getQuantity(), quantity)) 
															: (Math.subtractExact(inventoryService.getProducts().get(productName).getQuantity(), quantity));
						inventoryService.getProducts().get(productName).setQuantity(updateQuantity);
						inventoryService.getProducts().get(productName).setUpdatedTime(LocalDateTime.now());
                        if (isBuy) {
                        	inventoryService.getReport().getAvailableProducts().put(productName, inventoryService.getReport().getAvailableProducts().get(productName) + updateQuantity);
                        }
                        return true;
					}
				} else {
					System.out.println("Invalid Quantity");
				}
			} else {
				System.out.println("Invalid Product");
			}
		} else {
			System.out.println("Invalid command");
		}
		return false;
	}
	
	private Boolean updateSalePrice(String[] parsedInputArray) {
        String productName = parsedInputArray[1];
        Double newPrice = validatePrice(parsedInputArray[2]);
        if (newPrice != null) {
            Product productToUpdate = inventoryService.getProducts().get(productName);
            productToUpdate.setStatus(Status.OLD);
            productToUpdate.setUpdatedTime(LocalDateTime.now());
            String productNameWithoutVersion = productName.replaceAll("[^a-zA-Z]", "");
            String currentVersion = productName.replaceAll("[^0-9]", "");
            if (currentVersion == null || currentVersion.isEmpty()) {
                System.out.println("Product name don't have version");
                return false;
            }
            String newProductName = productNameWithoutVersion.concat("0") + (Integer.parseInt(currentVersion) + 1);

            Product versionedProduct = new Product();
            versionedProduct.setProductName(newProductName);
            versionedProduct.setTrade(new Trade(productToUpdate.getTrade().getBuyAt(), newPrice));
            versionedProduct.setQuantity(productToUpdate.getQuantity());
            versionedProduct.setStatus(Status.CURRENT);
            versionedProduct.setCreatedTime(LocalDateTime.now());
            versionedProduct.setUpdatedTime(LocalDateTime.now());
            inventoryService.getProducts().put(newProductName, versionedProduct);
            inventoryService.getReport().getAvailableProducts().put(newProductName, productToUpdate.getQuantity());
        }
        return false;
    }
	
	private Double validatePrice(String s) {
		boolean isValid = false;
		try {
			return Double.parseDouble(s);
		} catch (NumberFormatException e) {
			System.out.println("price is not double..");
		}
		return null;
	}
	
	private Integer validateQuantity(String s) {
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException e) {
			System.out.println("quantity is not integer..");
		}
		return null;
	}
	
	private Boolean validateProduct(String productName) {
		if (inventoryService.getProducts().containsKey(productName) 
				&& inventoryService.getProducts().get(productName).getStatus() == Status.CURRENT) {
			return true;
		} else {
			return false;
		}
	}

}
