package com.example.telurjago.Model;

public class Order {

    private String ProductId;
    private String ProductName;
    private String Quantity;
    private String Price;
    private String SellerId;

    public Order() {
    }


    public Order(String productId, String productName, String quantity, String price, String sid) {
        ProductId = productId;
        ProductName = productName;
        Quantity = quantity;
        Price = price;
        SellerId = sid;
    }

    public Order(String productID, String productName, String quantity, String price) {
    }

    public String getProductId() {
        return ProductId;
    }

    public void setProductId(String productId) {
        ProductId = productId;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getSid() {
        return SellerId;
    }

    public void setSid(String sid) {
        SellerId = sid;
    }
}
