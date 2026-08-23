package com.zybooks.inventoryapp_nicholaslindner;

public class InventoryItem {

    private final int id;
    private String itemName;
    private int quantity;

    public InventoryItem(int id, String itemName, int quantity) {
        this.id = id;
        this.itemName = itemName;
        this.quantity = quantity;
    }

    public int getId(){
        return id;
    }
    public String getItemName() {
        return itemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}//End
