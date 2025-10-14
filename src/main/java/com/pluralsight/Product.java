package com.pluralsight;

public class Product {
    private String sku;
    private String productName;
    private double price;

    public Product(String sku, String productName, double price) {
        this.sku = sku;
        this.productName = productName;
        this.price = price;
    }

    public String getSku() {
        return sku;
    }

    public String getProductName() {
        return productName;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return String.format("%s|%s|%.2f", this.sku, this.productName, this.price);
    }
}
