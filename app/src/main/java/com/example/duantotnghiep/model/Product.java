package com.example.duantotnghiep.model;

import java.util.List;

public class Product {

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", sellerId='" + sellerId + '\'' +
                ", name='" + name + '\'' +
                ", productType=" + productType +
                ", categoryID='" + categoryID + '\'' +
                ", brand='" + brand + '\'' +
                ", description='" + description + '\'' +
                ", imgProduct=" + imgProduct +
                ", listColorProduct=" + listColorProduct +
                ", sold=" + sold +
                ", reviewId='" + reviewId + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", size=" + size +
                ", selectedDiscounts=" + selectedDiscounts +
                ", isUserProduct=" + isUserProduct +
                ", selectedQuantity=" + selectedQuantity +
                '}';
    }
    private String id;
    private String sellerId;
    private String name;
    private ProductType productType;
    private String categoryID;
    private String brand;
    private String description;
    private List<String> imgProduct;
    private List<ColorProduct> listColorProduct;
    private int sold;
    private String reviewId;
    private int quantity;
    private double price;
    private List<String> size;
    private List<Discount> selectedDiscounts;
    private boolean isUserProduct;
    private int selectedQuantity;
    public Discount getDiscount() {
        return null;
    }
    public enum ProductType {
        CLOTHING,
        FOOTWEAR
    }
    public Product() {

    }
    public Product(String id, String sellerId, String name, ProductType productType, String categoryID, String brand, String description, List<String> imgProduct, List<ColorProduct> colorProduct, int sold, String reviewId, int quantity, double price, List<String> size, List<Discount> discounts) {
        this.id = id;
        this.sellerId = sellerId;
        this.name = name;
        this.productType = productType;
        this.categoryID = categoryID;
        this.brand = brand;
        this.description = description;
        this.imgProduct = imgProduct;
        this.listColorProduct = colorProduct;
        this.sold = sold;
        this.reviewId = reviewId;
        this.quantity = quantity;
        this.price = price;
        this.size = size;
        this.selectedDiscounts = discounts;
    }
    public List<Discount> getSelectedDiscounts() {
        return selectedDiscounts;
    }

    public void setSelectedDiscounts(List<Discount> selectedDiscounts) {
        this.selectedDiscounts = selectedDiscounts;
    }
    public int getSelectedQuantity() {
        return selectedQuantity;
    }
    public void setSelectedQuantity(int selectedQuantity) {
        this.selectedQuantity = selectedQuantity;
    }
    public boolean isUserProduct() {
        return isUserProduct;
    }
    public void setUserProduct(boolean userProduct) {
        isUserProduct = userProduct;
    }
    public List<String> getSize() {
        return size;
    }
    public void setSize(List<String> size) {
        this.size = size;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getSellerId() {
        return sellerId;
    }
    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public ProductType getProductType() {
        return productType;
    }
    public void setProductType(ProductType productType) {
        this.productType = productType;
    }
    public String getCategoryID() {
        return categoryID;
    }
    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }
    public String getBrand() {
        return brand;
    }
    public void setBrand(String brand) {
        this.brand = brand;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public List<String> getImgProduct() {
        return imgProduct;
    }
    public void setImgProduct(List<String> imgProduct) {
        this.imgProduct = imgProduct;
    }
    public List<ColorProduct> getListColor() {
        return listColorProduct;
    }
    public void setListColor(List<ColorProduct> listColorProduct) {
        this.listColorProduct = listColorProduct;
    }
    public int getSold() {
        return sold;
    }
    public void setSold(int sold) {
        this.sold = sold;
    }
    public String getReviewId() {
        return reviewId;
    }
    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }
}