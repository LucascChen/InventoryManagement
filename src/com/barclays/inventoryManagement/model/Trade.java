package com.barclays.inventoryManagement.model;

public class Trade {
	
	private Double buyAt;
    private Double sellAt;
    
    
	public Trade() {
		
	}
	public Trade(Double buyAt, Double sellAt) {
		super();
		this.buyAt = buyAt;
		this.sellAt = sellAt;
	}
	public Double getBuyAt() {
		return buyAt;
	}
	public void setBuyAt(Double buyAt) {
		this.buyAt = buyAt;
	}
	public Double getSellAt() {
		return sellAt;
	}
	public void setSellAt(Double sellAt) {
		this.sellAt = sellAt;
	}
    
    

}
